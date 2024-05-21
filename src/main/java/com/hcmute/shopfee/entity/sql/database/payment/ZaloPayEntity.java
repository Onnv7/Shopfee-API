package com.hcmute.shopfee.entity.sql.database.payment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.identifier.ChildTransactionID;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;

@Entity
@Table(name = "zalopay")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
//@IdClass(ChildTransactionID.class)
public class ZaloPayEntity extends TransactionEntity {
//    @EmbeddedId
//    private ChildTransactionID id;

    @Column(name = "payment_url", columnDefinition = "TEXT")
    private String paymentUrl;

    @Column(name = "app_trans_id")
    private String appTransactionId;

    @Column(name = "zp_trans_id")
    private String zalopayTransactionId;

//    @Id
//    @MapsId("transactionId")
//    @OneToOne
//    @JoinColumn(name = "transaction_id", nullable = false)
//    @JsonBackReference
//    private TransactionEntity transaction;
}
