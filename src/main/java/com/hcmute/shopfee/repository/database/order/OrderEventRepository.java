package com.hcmute.shopfee.repository.database.order;

import com.hcmute.shopfee.entity.order.OrderEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderEventRepository extends JpaRepository<OrderEventEntity, String> {
}
