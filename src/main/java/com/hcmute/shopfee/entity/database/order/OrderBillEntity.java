package com.hcmute.shopfee.entity.database.order;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.database.BranchEntity;
import com.hcmute.shopfee.entity.database.UserEntity;
import com.hcmute.shopfee.entity.database.coupon_used.CouponUsedEntity;
import com.hcmute.shopfee.entity.database.identifier.StringPrefixedSequenceGenerator;
import com.hcmute.shopfee.enums.OrderType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;

import static com.hcmute.shopfee.constant.EntityConstant.SEQUENCE_ID_GENERATOR;

@Entity
@Table(name = "order_bill")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OrderBillEntity {
    //    @GenericGenerator(name = "order_bill_id", strategy = TIME_ID_GENERATOR)
    @Id
    @GenericGenerator(name = "order_bill_id", strategy = SEQUENCE_ID_GENERATOR, parameters = {
            @Parameter(name = StringPrefixedSequenceGenerator.INCREMENT_PARAM, value = "1"),
            @Parameter(name = StringPrefixedSequenceGenerator.VALUE_PREFIX_PARAMETER, value = "OB"),
            @Parameter(name = StringPrefixedSequenceGenerator.NUMBER_FORMAT_PARAMETER, value = "%09d")
    })
    @GeneratedValue(generator = "order_bill_id")
    private String id;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private UserEntity user;

//    @Indexed(unique = true)
//    private String code;

    @Column(name = "note")
    private String note;

    @Column(name = "shipping_fee")
    private Long shippingFee;

    @Column(name = "total_item_price", nullable = false)
    private Long totalItemPrice;

    @Column(name = "coin")
    private Long coin;

    @Column(name = "total_payment", nullable = false)
    private Long totalPayment;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    private OrderType orderType;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonBackReference
    private BranchEntity branch;

    @Column(name = "receive_time")
    private Date receiveTime;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;
    // =================================================

    @OneToMany(mappedBy = "orderBill", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JsonManagedReference
    @OrderBy("createdAt DESC")
    private List<OrderEventEntity> orderEventList;

    @OneToMany(mappedBy = "orderBill", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private List<OrderItemEntity> orderItemList;

    @OneToMany(mappedBy = "orderBill", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private List<CouponUsedEntity> couponUsedList;

    @OneToOne(mappedBy = "orderBill", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private ShippingInformationEntity shippingInformation;

    @OneToOne(mappedBy = "orderBill", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private TransactionEntity transaction;

    @OneToOne(mappedBy = "orderBill", cascade = {CascadeType.MERGE})
    @JsonManagedReference
    private CancellationRequestEntity requestCancellation;
}
