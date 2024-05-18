package com.hcmute.shopfee.entity.sql.database.payment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.enums.TransactionStatus;
import com.hcmute.shopfee.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;

@Entity
@Table(name = "transaction")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TransactionEntity {
    @Id
    @GenericGenerator(name = "transaction_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "transaction_id")
    private String id;

    @OneToOne
    @JoinColumn(name = "order_bill_id", nullable = false)
    @JsonBackReference
    private OrderBillEntity orderBill;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @Column(name = "total_paid", nullable = false)
    private Long totalPaid;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    // =================================================================

    @OneToOne(mappedBy = "transaction", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private VNPayEntity vnPay;

    @OneToOne(mappedBy = "transaction", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private ZaloPayEntity zaloPay;
}
