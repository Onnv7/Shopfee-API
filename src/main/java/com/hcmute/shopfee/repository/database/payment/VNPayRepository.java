package com.hcmute.shopfee.repository.database.payment;

import com.hcmute.shopfee.entity.sql.database.payment.VNPayEntity;
import com.hcmute.shopfee.entity.sql.database.payment.ZaloPayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VNPayRepository extends JpaRepository<VNPayEntity, String> {
    Optional<VNPayEntity> findByInvoiceCode(String invoiceCode);
}
