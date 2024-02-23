package com.hcmute.shopfee.entity.database.coupon_used;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.database.coupon.CouponEntity;
import com.hcmute.shopfee.entity.database.order.OrderBillEntity;
import com.hcmute.shopfee.enums.CouponType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "coupon_used")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponUsedEntity {
    @Id
    @GenericGenerator(name = "coupon_used_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "coupon_used_id")
    private String id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "type", nullable = false)
    private CouponType type;

    @ManyToOne
    @JoinColumn(name = "order_bill_id", nullable = false)
    @JsonBackReference
    private OrderBillEntity orderBill;

    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = false)
    @JsonBackReference
    private CouponEntity coupon;

    // =================================================
    @OneToOne(mappedBy = "couponUsed", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private CouponRewardReceivedEntity couponRewardReceived;

}
