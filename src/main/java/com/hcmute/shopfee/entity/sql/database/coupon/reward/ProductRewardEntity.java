package com.hcmute.shopfee.entity.sql.database.coupon.reward;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponRewardEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "product_reward")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRewardEntity {
    @Id
    @GenericGenerator(name = "product_reward_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "product_reward_id")
    private String id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "product_size")
    private String productSize;

    @Column(name = "quantity", nullable = false)
    private Short quantity;

    @ManyToOne
    @JoinColumn(name = "coupon_reward_id")
    @JsonBackReference
    private CouponRewardEntity couponReward;
}
