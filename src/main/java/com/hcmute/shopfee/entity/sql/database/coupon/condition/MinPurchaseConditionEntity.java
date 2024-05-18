package com.hcmute.shopfee.entity.sql.database.coupon.condition;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponConditionEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "min_purchase_order_condition")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MinPurchaseConditionEntity {
    @Id
    @GenericGenerator(name = "min_purchase_order_condition_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "min_purchase_order_condition_id")
    private String id;

    @Column(name = "value", nullable = false)
    private Long value;

    @OneToOne
    @JoinColumn(name = "coupon_condition_id", nullable = false)
    @JsonBackReference
    private CouponConditionEntity couponCondition;
}
