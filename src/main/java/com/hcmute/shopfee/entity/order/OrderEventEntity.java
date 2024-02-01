package com.hcmute.shopfee.entity.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "order_event")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OrderEventEntity {
    @Id
    @GenericGenerator(name = "order_event_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "order_event_id")
    private String id;


    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

//    @Column(name = "time", nullable = false)
//    private Date time;

    @Column(name = "description", nullable = false)
    private String description;


    @Column(name = "is_employee", nullable = false)
    private boolean isEmployee;

    @CreatedBy
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "order_bill_id", nullable = false)
    @JsonBackReference
    private OrderBillEntity orderBill;
}
