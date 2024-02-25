package com.hcmute.shopfee.repository.database.order;

import com.hcmute.shopfee.entity.database.order.OrderEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderEventRepository extends JpaRepository<OrderEventEntity, String> {

    List<OrderEventEntity> findByOrderBill_IdOrderByCreatedAtDesc(String orderBillId);
}
