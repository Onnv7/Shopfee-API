package com.hcmute.shopfee.entity.sql.database.coupon_used;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import com.hcmute.shopfee.entity.sql.database.coupon_used.reward.MoneyRewardReceivedEntity;
import com.hcmute.shopfee.entity.sql.database.coupon_used.reward.ProductRewardReceivedEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import com.hcmute.shopfee.entity.sql.database.order.OrderBillEntity;
import com.hcmute.shopfee.enums.CouponRewardType;
import com.hcmute.shopfee.enums.CouponType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;


@Entity
@Table(name = "coupon_used")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponUsedEntity {
    @Id
    @GenericGenerator(name = "coupon_used_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "coupon_used_id")
    @Column(length = 16)
    private String id;

    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponType type;

    @ManyToOne
    @JoinColumn(name = "order_bill_id", nullable = false)
    @JsonBackReference
    private OrderBillEntity orderBill;

    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = false)
    @JsonBackReference
    private CouponEntity coupon;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false)
    private CouponRewardType rewardType;

    // =================================================

    @OneToOne(mappedBy = "couponUsed", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private MoneyRewardReceivedEntity moneyRewardReceived;

    @OneToMany(mappedBy = "couponUsed", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private List<ProductRewardReceivedEntity> productRewardReceivedList;

}
