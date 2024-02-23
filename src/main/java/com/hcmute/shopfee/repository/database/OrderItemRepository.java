package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.database.order.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, String> {
}
