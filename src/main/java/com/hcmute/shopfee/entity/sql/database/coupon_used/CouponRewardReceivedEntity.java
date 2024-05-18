package com.hcmute.shopfee.entity.sql.database.coupon_used;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.sql.database.coupon_used.reward.MoneyRewardReceivedEntity;
import com.hcmute.shopfee.entity.sql.database.coupon_used.reward.ProductRewardReceivedEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import com.hcmute.shopfee.enums.CouponRewardType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;


@Entity
@Table(name = "coupon_reward_received")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponRewardReceivedEntity {
    @Id
    @GenericGenerator(name = "coupon_reward_received_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "coupon_reward_received_id")
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
