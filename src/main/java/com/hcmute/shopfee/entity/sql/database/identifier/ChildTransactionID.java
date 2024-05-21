package com.hcmute.shopfee.entity.sql.database.identifier;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.payment.TransactionEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ChildTransactionID implements Serializable{
    @Column(name = "transaction_id", nullable = false)
    public String transactionId;

//    @OneToOne
//    @JoinColumn(name = "transaction_id", nullable = false)
//    @JsonBackReference
//    private TransactionEntity transaction;
}
