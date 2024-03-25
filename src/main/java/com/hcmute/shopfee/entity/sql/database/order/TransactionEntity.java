package com.hcmute.shopfee.entity.sql.database.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.enums.PaymentStatus;
import com.hcmute.shopfee.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

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
    @GenericGenerator(name = "transaction_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "transaction_id")
    private String id;

    @OneToOne
    @JoinColumn(name = "order_bill_id", nullable = false)
    @JsonBackReference
    private OrderBillEntity orderBill;

    @Column(name = "invoice_code")
    private String invoiceCode;

    @Column(name = "time_code")
    private String timeCode;

    @Column(name = "payment_url", columnDefinition = "TEXT")
    private String paymentUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @Column(name = "is_refunded")
    private boolean isRefunded;

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
}
