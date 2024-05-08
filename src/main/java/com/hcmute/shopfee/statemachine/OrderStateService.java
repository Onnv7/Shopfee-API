package com.hcmute.shopfee.statemachine;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.OrderType;
import com.hcmute.shopfee.enums.TransactionStatus;
import com.hcmute.shopfee.enums.PaymentType;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.order.OrderEventRepository;
import com.hcmute.shopfee.repository.database.payment.TransactionRepository;
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

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class OrderStateService {
    public static final String ORDER_ID_HEADER = "order_id";
    public static final String ORDER_TYPE_HEADER = "order_type";
    public static final String PAYMENT_TYPE_HEADER = "payment_type";
    public static final String NOTE_HEADER = "note";
    public static final String TRANSACTION_STATUS_HEADER = "transaction_status";
    private final OrderEventRepository orderEventRepository;
    private final StateMachineFactory<OrderStatus, OrderEvent> stateMachineFactory;
    private final OrderBillRepository orderBillRepository;
    private final OrderStateMachineInterceptor orderStateMachineInterceptor;
    private final TransactionRepository transactionRepository;

    @Transactional
    public boolean sendMonoEvent(String orderId, String note, OrderEvent event) {
        StateMachine<OrderStatus, OrderEvent> sm = build(orderId);
        OrderType orderType = orderBillRepository.getOrderType(orderId);
        PaymentType paymentType = transactionRepository.getPaymentTypeByOrderId(orderId);
        TransactionStatus transactionStatus = transactionRepository.getTransactionStatusByOrderId(orderId);
        Message<OrderEvent> message = MessageBuilder.withPayload(event)
                .setHeader(ORDER_ID_HEADER, orderId)
                .setHeader(NOTE_HEADER, note)
                .setHeader(NOTE_HEADER, note)
                .setHeader(ORDER_TYPE_HEADER, orderType)
                .setHeader(TRANSACTION_STATUS_HEADER, transactionStatus)
                .setHeader(PAYMENT_TYPE_HEADER, paymentType)
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
                .setHeader(ORDER_ID_HEADER, orderId)
                .setHeader(NOTE_HEADER, desc)
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
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.ORDER_BILL_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + orderId));

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
