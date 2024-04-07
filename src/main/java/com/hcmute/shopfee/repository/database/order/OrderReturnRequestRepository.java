package com.hcmute.shopfee.repository.database.order;

import com.hcmute.shopfee.entity.sql.database.order.OrderRefundRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderReturnRequestRepository extends JpaRepository<OrderRefundRequestEntity, String> {
    Optional<OrderRefundRequestEntity> findByOrderBill_Id(String orderBillId);
}
