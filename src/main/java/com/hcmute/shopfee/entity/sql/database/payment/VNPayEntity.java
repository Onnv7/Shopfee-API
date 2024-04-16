package com.hcmute.shopfee.entity.sql.database.payment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "vnpay")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class VNPayEntity {
    @Id
    @GenericGenerator(name = "vnpay_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "vnpay_id")
    private String id;

    @Column(name = "payment_url", columnDefinition = "TEXT")
    private String paymentUrl;

    @Column(name = "invoice_code")
    private String invoiceCode;

    @Column(name = "time_code")
    private String timeCode;

    @OneToOne//(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "transaction_id", nullable = false)
    @JsonBackReference
    private TransactionEntity transaction;
}
