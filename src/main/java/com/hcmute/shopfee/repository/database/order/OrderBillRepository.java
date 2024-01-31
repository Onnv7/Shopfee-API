package com.hcmute.shopfee.repository.database.order;

import com.hcmute.shopfee.entity.order.OrderBillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderBillRepository extends JpaRepository<OrderBillEntity, String> {
}
