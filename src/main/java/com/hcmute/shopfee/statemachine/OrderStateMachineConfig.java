package com.hcmute.shopfee.statemachine;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderEventEntity;
import com.hcmute.shopfee.enums.ActorType;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.OrderType;
import com.hcmute.shopfee.enums.Role;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.Optional;

import static com.hcmute.shopfee.statemachine.OrderStateService.NOTE_HEADER;
import static com.hcmute.shopfee.statemachine.OrderStateService.ORDER_ID_HEADER;

@Slf4j
@EnableStateMachineFactory
//@EnableStateMachine
@Configuration
public class OrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderStatus, OrderEvent> {
    private final OrderBillRepository orderBillRepository;

    public OrderStateMachineConfig(OrderBillRepository orderBillRepository) {
        this.orderBillRepository = orderBillRepository;
    }

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderEvent> states) throws Exception {
        states.withStates()
                .initial(OrderStatus.CREATED)
                .states(EnumSet.allOf(OrderStatus.class))
                .end(OrderStatus.NOT_RECEIVED)
                .end(OrderStatus.CANCELED)
                .end(OrderStatus.SUCCEED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderEvent> transitions) throws Exception {
         OrderTypeValidationGuard orderTypeValidationGuard = new OrderTypeValidationGuard();

        transitions
                .withExternal().source(OrderStatus.CREATED).target(OrderStatus.CANCELED).event(OrderEvent.PAYMENT_FAILED).action(processOrder()).guard(orderTypeValidationGuard.validateBankingPayment())
                .and()
                .withExternal().source(OrderStatus.CREATED).target(OrderStatus.ACCEPTED).event(OrderEvent.ORDER_ACCEPT).action(processOrder()).guard(orderTypeValidationGuard.validateTransaction())
                .and()
                .withExternal().source(OrderStatus.CREATED).target(OrderStatus.CANCELED).event(OrderEvent.ORDER_REFUSE).action(processOrder())
                .and()
                .withExternal().source(OrderStatus.CREATED).target(OrderStatus.CANCELED).event(OrderEvent.EMPLOYEE_ORDER_REFUSE).action(processOrder())
                .and()
                .withExternal().source(OrderStatus.ACCEPTED).target(OrderStatus.CANCELLATION_REQUEST).event(OrderEvent.CANCEL_REQUEST).action(processOrder())
                .and()
                .withExternal().source(OrderStatus.ACCEPTED).target(OrderStatus.PREPARED).event(OrderEvent.READY_SHIPPING).action(processOrder())
                .and()
                .withExternal().source(OrderStatus.CANCELLATION_REQUEST).target(OrderStatus.CANCELLATION_REQUEST_ACCEPTED).event(OrderEvent.CANCEL_REQUEST_ACCEPT).action(processOrder())

                .and()
                .withExternal().source(OrderStatus.CANCELLATION_REQUEST).target(OrderStatus.CANCELLATION_REQUEST_REFUSED).event(OrderEvent.CANCEL_REQUEST_REFUSE).action(processOrder())
                .and()
                .withExternal().source(OrderStatus.CANCELLATION_REQUEST_REFUSED).target(OrderStatus.PREPARED).event(OrderEvent.READY_SHIPPING).action(processOrder())
                // shipping
                .and()
                .withExternal().source(OrderStatus.PREPARED).target(OrderStatus.DELIVERING).event(OrderEvent.START_SHIPPING).action(processOrder()).guard(orderTypeValidationGuard.validateOrderType(OrderType.SHIPPING))
                .and()
                .withExternal().source(OrderStatus.DELIVERING).target(OrderStatus.NOT_RECEIVED).event(OrderEvent.ORDER_BOOM).action(processOrder()).guard(orderTypeValidationGuard.validateOrderType(OrderType.SHIPPING))
                .and()
                .withExternal().source(OrderStatus.DELIVERING).target(OrderStatus.SUCCEED).event(OrderEvent.ORDER_FULFILL).action(processOrder()).guard(orderTypeValidationGuard.validateOrderType(OrderType.SHIPPING))
                // onsite
                .and()
                .withExternal().source(OrderStatus.PREPARED).target(OrderStatus.NOT_RECEIVED).event(OrderEvent.ORDER_BOOM).action(processOrder()).guard(orderTypeValidationGuard.validateOrderType(OrderType.ONSITE))
                .and()
                .withExternal().source(OrderStatus.PREPARED).target(OrderStatus.SUCCEED).event(OrderEvent.ORDER_FULFILL).action(processOrder()).guard(orderTypeValidationGuard.validateOrderType(OrderType.ONSITE));

    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStatus, OrderEvent> config) throws Exception {
        StateMachineListenerAdapter<OrderStatus, OrderEvent> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<OrderStatus, OrderEvent> from, State<OrderStatus, OrderEvent> to) {
                log.info(String.format("State changed from %s to %s", from, to));
            }
        };
        config.withConfiguration().listener(adapter);
    }

    public Action<OrderStatus, OrderEvent> processOrder() {
        return context -> {
            Optional.ofNullable(context.getMessage()).ifPresent(msg -> {
                OrderStatus orderStatus = context.getTarget().getId();
                String note = msg.getHeaders().getOrDefault(NOTE_HEADER, null).toString();
                ActorType actorType = SecurityUtils.getRoleList().contains(Role.ROLE_USER.name()) ? ActorType.USER :
                        SecurityUtils.getRoleList().contains(Role.ROLE_WAITER.name()) ? ActorType.EMPLOYEE : ActorType.AUTOMATIC;
                Optional.ofNullable(msg.getHeaders().getOrDefault(ORDER_ID_HEADER, ""))
                        .ifPresent(orderId -> {
                            OrderBillEntity orderBill = orderBillRepository.findById(orderId.toString())
                                    .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.ORDER_BILL_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + orderId));
                            OrderEventEntity orderEvent = OrderEventEntity.builder()
                                    .orderStatus(orderStatus)
                                    .note(note)
                                    .description(orderStatus.getResultDescription())
                                    .actor(actorType)
                                    .orderBill(orderBill)
                                    .build();
                            orderBill.getOrderEventList().add(orderEvent);
                            orderBillRepository.save(orderBill);

                            if (orderStatus == OrderStatus.CANCELLATION_REQUEST_ACCEPTED) {
                                OrderEventEntity orderEvent2 = OrderEventEntity.builder()
                                        .orderStatus(OrderStatus.CANCELED)
                                        .note(note)
                                        .description(OrderStatus.CANCELED.getResultDescription())
                                        .actor(ActorType.EMPLOYEE)
                                        .orderBill(orderBill)
                                        .build();
                                orderBill.getOrderEventList().add(orderEvent2);
                                orderBillRepository.save(orderBill);
                            }
                        });
            });
        };
    }
}
