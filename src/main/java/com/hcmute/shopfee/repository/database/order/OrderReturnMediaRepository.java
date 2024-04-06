package com.hcmute.shopfee.repository.database.order;

import com.hcmute.shopfee.entity.sql.database.order.OrderReturnMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderReturnMediaRepository extends JpaRepository<OrderReturnMediaEntity, String> {
}
