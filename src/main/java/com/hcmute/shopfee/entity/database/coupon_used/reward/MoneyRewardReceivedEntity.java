package com.hcmute.shopfee.entity.database.coupon_used.reward;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.database.coupon_used.CouponRewardReceivedEntity;
import com.hcmute.shopfee.enums.MoneyRewardUnit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "money_reward_received")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoneyRewardReceivedEntity {
    @Id
    @GenericGenerator(name = "money_reward_received_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "money_reward_received_id")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private MoneyRewardUnit unit;

//    @Column(name = "coupon_type", nullable = false)
//    private CouponType couponType;
//    @Column(name = "target_reward", nullable = false)
//    private TargetReward targetReward;

    @Column(name = "value", nullable = false)
    private Integer value;

    @OneToOne
    @JoinColumn(name = "coupon_reward_received_id")
    @JsonBackReference
    private CouponRewardReceivedEntity couponRewardReceived;


//    @OneToOne
//    @JoinColumn(name = "coupon_used_id")
//    @JsonBackReference
//    private CouponUsedEntity couponUsed;
}
