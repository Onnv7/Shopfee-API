package com.hcmute.shopfee.state_machine.domain;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderEventEntity;
import com.hcmute.shopfee.enums.ActorType;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptor;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class OrderStateMachineInterceptor extends StateMachineInterceptorAdapter<OrderStatus, OrderEvent> {

    private final OrderBillRepository orderBillRepository;


    @Override
    public void preStateChange(State<OrderStatus, OrderEvent> state, Message<OrderEvent> message, Transition<OrderStatus, OrderEvent> transition, StateMachine<OrderStatus, OrderEvent> stateMachine, StateMachine<OrderStatus, OrderEvent> rootStateMachine) {
        System.out.println("preStateChange");
        Optional.ofNullable(message).ifPresent(msg -> {
            Optional.ofNullable(msg.getHeaders().getOrDefault(SateService.ORDER_HEADER, ""))
                    .ifPresent(orderId -> {
                        OrderBillEntity orderBill = orderBillRepository.findById(orderId.toString())
                                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.ORDER_BILL_ID_NOT_FOUND + orderId));

                        OrderEventEntity orderEvent = OrderEventEntity.builder()
                                .orderStatus(state.getId())
                                .description("description")
                                .actor(ActorType.AUTOMATIC)
                                .createdBy("tester")
                                .orderBill(orderBill)
                                .build();

                        orderBill.getOrderEventList().add(orderEvent);
                        orderBillRepository.save(orderBill);
                    });
        });
    }

    @Override
    public Message<OrderEvent> preEvent(Message<OrderEvent> message, StateMachine<OrderStatus, OrderEvent> stateMachine) {
        return super.preEvent(message, stateMachine);
    }

    @Override
    public void postStateChange(State<OrderStatus, OrderEvent> state, Message<OrderEvent> message, Transition<OrderStatus, OrderEvent> transition, StateMachine<OrderStatus, OrderEvent> stateMachine, StateMachine<OrderStatus, OrderEvent> rootStateMachine) {
        super.postStateChange(state, message, transition, stateMachine, rootStateMachine);
    }

    @Override
    public StateContext<OrderStatus, OrderEvent> preTransition(StateContext<OrderStatus, OrderEvent> stateContext) {
        return super.preTransition(stateContext);
    }

    @Override
    public StateContext<OrderStatus, OrderEvent> postTransition(StateContext<OrderStatus, OrderEvent> stateContext) {
        return super.postTransition(stateContext);
    }
}
