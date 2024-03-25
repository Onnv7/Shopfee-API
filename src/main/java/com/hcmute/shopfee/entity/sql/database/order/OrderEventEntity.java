package com.hcmute.shopfee.entity.sql.database.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.listener.OrderEventListener;
import com.hcmute.shopfee.enums.ActorType;
import com.hcmute.shopfee.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "order_event")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class, OrderEventListener.class})
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

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;


    @Enumerated(EnumType.STRING)
    @Column(name = "actor", nullable = false)
    private ActorType actor;

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
