package com.hcmute.shopfee.entity.sql.database.payment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "zalopay")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ZaloPayEntity {
    @Id
    @GenericGenerator(name = "zalopay_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "zalopay_id")
    private String id;

    @Column(name = "app_trans_id")
    private String appTransactionId;

    @Column(name = "zp_trans_id")
    private Long zalopayTransactionId;

    @OneToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    @JsonBackReference
    private TransactionEntity transaction;
}
