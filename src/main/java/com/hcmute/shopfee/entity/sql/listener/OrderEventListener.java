package com.hcmute.shopfee.entity.sql.listener;

import com.hcmute.shopfee.constant.ShopfeeConstant;
import com.hcmute.shopfee.entity.sql.database.CoinHistoryEntity;
import com.hcmute.shopfee.entity.sql.database.UserEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderEventEntity;
import com.hcmute.shopfee.entity.sql.database.order.TransactionEntity;
import com.hcmute.shopfee.enums.ActorType;
import com.hcmute.shopfee.enums.OrderStatus;
import com.hcmute.shopfee.enums.PaymentStatus;
import com.hcmute.shopfee.repository.database.CoinHistoryRepository;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.repository.database.order.OrderEventRepository;
import com.hcmute.shopfee.service.common.AuditorAwareService;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final AuditorAwareService auditorAwareService;

    @Autowired
    @Lazy
    private OrderEventRepository orderEventRepository;

    @Autowired
    @Lazy
    private CoinHistoryRepository coinHistoryRepository;

    @Autowired
    @Lazy
    private OrderBillRepository orderBillRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @PostPersist
    public void postPersist(OrderEventEntity orderEvent) {
        OrderBillEntity orderBill = orderEvent.getOrderBill();
        TransactionEntity transaction = orderBill.getTransaction();
        OrderStatus orderStatus = orderEvent.getOrderStatus();
        OrderEventEntity newOrderEvent = OrderEventEntity.builder()
                .orderBill(orderEvent.getOrderBill())
                .actor(ActorType.AUTOMATIC)
                .createdBy(auditorAwareService.getCurrentAuditor().orElse("AUTOMATIC"))
                .build();
        if (orderStatus == OrderStatus.CANCELLATION_REQUEST_ACCEPTED) {
            newOrderEvent.setOrderStatus(OrderStatus.CANCELED);
            newOrderEvent.setDescription("The order has been cancelled");
        } else if(orderStatus == OrderStatus.NOT_RECEIVED) {
            newOrderEvent.setOrderStatus(OrderStatus.CANCELED);
            newOrderEvent.setDescription("The order was canceled because it could not be delivered to the customer");
        } else if(orderStatus == OrderStatus.DELIVERED) {
            newOrderEvent.setOrderStatus(OrderStatus.SUCCEED);
            newOrderEvent.setDescription("Order completed successfully");
        }
        if(newOrderEvent.getOrderStatus() == OrderStatus.CANCELED) {
            long coinRefunded = 0L;
            if(transaction.getStatus() == PaymentStatus.PAID) {
                coinRefunded += transaction.getTotalPaid();
                transaction.setRefunded(true);
            } else if(transaction.getStatus() == PaymentStatus.UNPAID && orderBill.getCoin() != null) {
                coinRefunded += orderBill.getCoin();
            }
            if(coinRefunded > 0) {
                UserEntity user = orderBill.getUser();
                user.setCoin(user.getCoin() + coinRefunded);

                CoinHistoryEntity coinHistory = CoinHistoryEntity.builder()
                        .coin(coinRefunded)
                        .actor(ActorType.AUTOMATIC)
                        .user(user)
                        .description(ShopfeeConstant.COIN_REFUND_CANCELLED_ORDER)
                        .build();

                coinHistoryRepository.save(coinHistory);
            }

        }
        orderBill.getOrderEventList().add(newOrderEvent);
        orderBillRepository.save(orderBill);
    }
}
