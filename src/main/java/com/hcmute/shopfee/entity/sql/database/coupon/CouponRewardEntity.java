package com.hcmute.shopfee.entity.sql.database.coupon;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.sql.database.coupon.reward.MoneyRewardEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.reward.ProductRewardEntity;
import com.hcmute.shopfee.enums.CouponRewardType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "coupon_reward")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponRewardEntity {
    @Id
    @GenericGenerator(name = "coupon_reward_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "coupon_reward_id")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CouponRewardType type;

    @OneToOne
    @JoinColumn(name = "coupon_id")
    @JsonBackReference
    private CouponEntity coupon;

    // =================================================================
    @OneToOne(mappedBy = "couponReward", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private MoneyRewardEntity moneyReward;

    @OneToMany(mappedBy = "couponReward", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private List<ProductRewardEntity> productRewardList;

}
