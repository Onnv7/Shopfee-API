package com.hcmute.shopfee.repository.database.payment;

import com.hcmute.shopfee.entity.sql.database.identifier.ChildTransactionID;
import com.hcmute.shopfee.entity.sql.database.payment.ZaloPayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZaloPayRepository extends JpaRepository<ZaloPayEntity, ChildTransactionID> {
    Optional<ZaloPayEntity> findByAppTransactionId(String appTransactionId);
}
