package com.hcmute.shopfee.repository.database.payment;

import com.hcmute.shopfee.entity.sql.database.identifier.ChildTransactionID;
import com.hcmute.shopfee.entity.sql.database.payment.VNPayEntity;
import com.hcmute.shopfee.entity.sql.database.payment.ZaloPayEntity;
import jakarta.persistence.IdClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VNPayRepository extends JpaRepository<VNPayEntity, ChildTransactionID> {
    Optional<VNPayEntity> findByInvoiceCode(String invoiceCode);
}
