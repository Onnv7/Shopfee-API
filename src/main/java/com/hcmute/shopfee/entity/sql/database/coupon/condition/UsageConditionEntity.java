package com.hcmute.shopfee.entity.sql.database.coupon.condition;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponConditionEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import com.hcmute.shopfee.enums.UsageConditionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "usage_condition")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsageConditionEntity {
    @Id
    @GenericGenerator(name = "usage_condition_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "usage_condition_id")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private UsageConditionType type;

    @Column(name = "value")
    private Integer value;

    @ManyToOne
    @JoinColumn(name = "coupon_condition_id", nullable = false)
    @JsonBackReference
    private CouponConditionEntity couponCondition;
}
