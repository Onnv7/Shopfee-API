package com.hcmute.shopfee.entity.sql.database.order;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.sql.database.admin.BranchEntity;
import com.hcmute.shopfee.entity.sql.database.user.UserEntity;
import com.hcmute.shopfee.entity.sql.database.coupon_used.CouponUsedEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.SeqIdentifierGenerator;
import com.hcmute.shopfee.entity.sql.database.payment.TransactionEntity;
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

@Entity
@Table(name = "order_bill")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OrderBillEntity {
    @Id
    @GenericGenerator(name = "order_bill_id", type = SeqIdentifierGenerator.class, parameters = {
            @Parameter(name = SeqIdentifierGenerator.ENTITY_NAME_PARAMETER, value = "OrderBillEntity"),
            @Parameter(name = SeqIdentifierGenerator.VALUE_PREFIX_PARAMETER, value = "OB"),
            @Parameter(name = SeqIdentifierGenerator.NUMBER_FORMAT_PARAMETER, value = "%09d")
    })
    @GeneratedValue(generator = "order_bill_id")
    @Column(length = 10)
    private String id;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private UserEntity user;

    @Column(name = "note")
    private String note;

    @Column(name = "shipping_fee")
    private Long shippingFee;

    @Column(name = "shipping_discount")
    private Long shippingDiscount;

    @Column(name = "total_item_price", nullable = false)
    private Long totalItemPrice;

    @Column(name = "order_discount")
    private Long orderDiscount;

    @Column(name = "coin")
    private Long coin;

    @Column(name = "total_payment", nullable = false)
    private Long totalPayment;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    private OrderType orderType;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    @JsonBackReference
    private BranchEntity branch;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "transaction_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private TransactionEntity transaction;

    @OneToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "receiver_information_id", nullable = false)
    @JsonBackReference
    private ReceiverInformationEntity receiverInformation;

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

    @OneToMany(mappedBy = "orderBill", cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<OrderItemEntity> orderItemList;

    @OneToMany(mappedBy = "orderBill", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private List<CouponUsedEntity> couponUsedList;

    @OneToOne(mappedBy = "orderBill", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private OrderRefundRequestEntity orderRefundRequest;
}
