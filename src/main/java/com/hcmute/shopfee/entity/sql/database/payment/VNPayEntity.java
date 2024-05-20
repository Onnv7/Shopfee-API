package com.hcmute.shopfee.entity.sql.database.payment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.identifier.ChildTransactionID;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;

@Entity
@Table(name = "vnpay")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class VNPayEntity {
    @EmbeddedId
    private ChildTransactionID id;

    @Column(name = "payment_url", columnDefinition = "TEXT")
    private String paymentUrl;

    @Column(name = "invoice_code")
    private String invoiceCode;

    @Column(name = "time_code")
    private String timeCode;

    @MapsId("transactionId")
    @OneToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    @JsonBackReference
    private TransactionEntity transaction;
}
