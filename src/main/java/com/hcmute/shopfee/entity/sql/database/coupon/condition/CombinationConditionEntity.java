package com.hcmute.shopfee.entity.sql.database.coupon.condition;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponConditionEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import com.hcmute.shopfee.enums.CouponType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "combination_condition")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CombinationConditionEntity {
    @Id
    @GenericGenerator(name = "combination_condition_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "combination_condition_id")
    @Column(length = 16)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CouponType type;

    @ManyToOne
    @JoinColumn(name = "coupon_condition_id", nullable = false)
    @JsonBackReference
    private CouponConditionEntity couponCondition;
}
