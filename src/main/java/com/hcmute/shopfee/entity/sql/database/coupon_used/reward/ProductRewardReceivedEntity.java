package com.hcmute.shopfee.entity.sql.database.coupon_used.reward;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.coupon_used.CouponRewardReceivedEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "product_reward_received")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRewardReceivedEntity {
    @Id
    @GenericGenerator(name = "product_reward_received_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "product_reward_received_id")
    private String id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "quantity", nullable = false)
    private Short quantity;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_size", nullable = false)
    private String productSize;

    @ManyToOne
    @JoinColumn(name = "coupon_reward_received_id")
    @JsonBackReference
    private CouponRewardReceivedEntity couponRewardReceived;
}
