package com.hcmute.shopfee.entity.sql.database.identifier;

import com.hcmute.shopfee.entity.sql.database.payment.TransactionEntity;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ChildTransactionID {
//    private TransactionEntity transaction;
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;
}
