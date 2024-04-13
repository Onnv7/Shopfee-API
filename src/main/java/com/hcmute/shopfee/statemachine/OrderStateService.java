package com.hcmute.shopfee.statemachine;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.OrderType;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.order.OrderEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class OrderStateService {
    public static final String ORDER_HEADER = "order_id";
    public static final String ORDER_TYPE_HEADER = "order_type";
    public static final String DESC_HEADER = "desc";
    private final OrderEventRepository orderEventRepository;
    private final StateMachineFactory<OrderStatus, OrderEvent> stateMachineFactory;
    private final OrderBillRepository orderBillRepository;
    private final OrderStateMachineInterceptor orderStateMachineInterceptor;

    @Transactional
    public boolean sendMonoEvent(String orderId, String desc, OrderEvent event) {
        StateMachine<OrderStatus, OrderEvent> sm = build(orderId);
        OrderType orderType = orderBillRepository.getOrderType(orderId);
        Message<OrderEvent> message = MessageBuilder.withPayload(event)
                .setHeader(ORDER_HEADER, orderId)
                .setHeader(DESC_HEADER, desc)
                .setHeader(ORDER_TYPE_HEADER, orderType)
                .build();
        AtomicBoolean sendSuccess = new AtomicBoolean(true);
        Flux<StateMachineEventResult<OrderStatus, OrderEvent>> se = sm.sendEvent(Mono.just(message)).log();
        se.subscribe(it -> {
            if(it.getResultType() != StateMachineEventResult.ResultType.ACCEPTED) {
                sendSuccess.set(false);
            }
        });

        return (!sm.hasStateMachineError() &&  sendSuccess.get());
    }

    public Mono<OrderStatus> sendEventMono(String orderId, String desc, OrderEvent event) {
        StateMachine<OrderStatus, OrderEvent> sm = build(orderId);
        OrderType orderType = orderBillRepository.getOrderType(orderId);
        Message<OrderEvent> message = MessageBuilder.withPayload(event)
                .setHeader(ORDER_HEADER, orderId)
                .setHeader(DESC_HEADER, desc)
                .setHeader(ORDER_TYPE_HEADER, orderType)
                .build();
        Flux<StateMachineEventResult<OrderStatus, OrderEvent>> se = sm.sendEvent(Mono.just(message)).map(result -> {
            if (result.getResultType() == StateMachineEventResult.ResultType.ACCEPTED) {
                System.out.println("Sự kiện đã được gửi qua guard");
            } else {
                System.out.println("Sự kiện không được gửi qua guard. Lý do: " + result.getMessage());
            }
            return result;
        }).log();
        se.subscribe();

        Mono<OrderStatus> rs = se.then(Mono.defer(() -> Mono.just(sm.getState().getId())));
        return rs;

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
