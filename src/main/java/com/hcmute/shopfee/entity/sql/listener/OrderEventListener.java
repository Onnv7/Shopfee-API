package com.hcmute.shopfee.entity.sql.listener;

import com.hcmute.shopfee.entity.sql.database.order.OrderEventEntity;
import com.hcmute.shopfee.enums.ActorType;
import com.hcmute.shopfee.enums.OrderStatus;
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @PostPersist
    public void postPersist(OrderEventEntity orderEvent) {
        if (orderEvent.getOrderStatus() == OrderStatus.CANCELLATION_REQUEST_ACCEPTED) {
            OrderEventEntity newEvent = OrderEventEntity.builder()
                    .orderBill(orderEvent.getOrderBill())
                    .description("The order has been cancelled")
                    .orderStatus(OrderStatus.CANCELED)
                    .actor(ActorType.AUTOMATIC)
                    .createdBy(auditorAwareService.getCurrentAuditor().orElse("AUTOMATIC"))
                    .build();
//            throw new RuntimeException();
            orderEventRepository.save(newEvent);
        }
    }
}
