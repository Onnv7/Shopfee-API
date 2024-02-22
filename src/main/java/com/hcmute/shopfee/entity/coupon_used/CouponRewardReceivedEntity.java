package com.hcmute.shopfee.entity.coupon_used;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.coupon.CouponEntity;
import com.hcmute.shopfee.entity.coupon.reward.MoneyRewardEntity;
import com.hcmute.shopfee.entity.coupon.reward.ProductRewardEntity;
import com.hcmute.shopfee.entity.coupon_used.reward.MoneyRewardReceivedEntity;
import com.hcmute.shopfee.entity.coupon_used.reward.ProductRewardReceivedEntity;
import com.hcmute.shopfee.entity.order.OrderItemEntity;
import com.hcmute.shopfee.enums.CouponRewardType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "coupon_reward_received")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponRewardReceivedEntity {
    @Id
    @GenericGenerator(name = "coupon_reward_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "coupon_reward_id")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CouponRewardType type;

    @OneToOne
    @JoinColumn(name = "coupon_used_id")
    @JsonBackReference
    private CouponUsedEntity couponUsed;

    // =================================================================
    @OneToOne(mappedBy = "couponRewardReceived", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private MoneyRewardReceivedEntity moneyRewardReceived;

    @OneToMany(mappedBy = "couponRewardReceived", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private List<ProductRewardReceivedEntity> productRewardReceivedList;

}
