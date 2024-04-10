package com.hcmute.shopfee.state_machine.domain;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.order.OrderEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SateService {
    public static final String ORDER_HEADER = "order_id";
    private final OrderEventRepository orderEventRepository;
    private final StateMachineFactory<OrderStatus, OrderEvent> stateMachineFactory;
    private final OrderBillRepository orderBillRepository;
    private final OrderStateMachineInterceptor orderStateMachineInterceptor;

    @Transactional
    public OrderBillEntity cancel(String orderBillId) {
        StateMachine<OrderStatus, OrderEvent> sm = build(orderBillId);

        sendEvent(orderBillId, sm, OrderEvent.CANCEL);
        return null;
    }


    private void sendEvent(String orderId, StateMachine<OrderStatus, OrderEvent> sm, OrderEvent event) {
        Message msg = MessageBuilder.withPayload(event)
                .setHeader(ORDER_HEADER, orderId)
                .build();
        sm.sendEvent(msg);
    }
    private StateMachine<OrderStatus, OrderEvent> build(String orderId) {
        OrderBillEntity orderBill = orderBillRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.ORDER_BILL_ID_NOT_FOUND + orderId));

        StateMachine<OrderStatus, OrderEvent> sm = stateMachineFactory.getStateMachine(orderBill.getId());

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(orderStateMachineInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(orderBill.getOrderEventList().get(0).getOrderStatus(), null, null, null));
                });

        sm.start();

        return sm;
    }
}
