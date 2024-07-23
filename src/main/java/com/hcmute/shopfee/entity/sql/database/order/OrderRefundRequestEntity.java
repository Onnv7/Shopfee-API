package com.hcmute.shopfee.entity.sql.database.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.enums.param.AnswerStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;

import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;

@Entity
@Table(name = "order_refund_request")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OrderRefundRequestEntity {
    @Id
    @GenericGenerator(name = "order_refund_request_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "order_refund_request_id")
    @Column(length = 16)
    private String id;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "note")
    private String note;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AnswerStatus status;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "order_bill_id", nullable = false)
    private OrderBillEntity orderBill;


    // =================================================

    @OneToMany(mappedBy = "orderRefundRequest", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private List<OrderRefundMediaEntity> orderRefundMediaList;

}
