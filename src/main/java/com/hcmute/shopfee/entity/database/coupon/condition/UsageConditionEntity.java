package com.hcmute.shopfee.entity.database.coupon.condition;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.database.coupon.CouponConditionEntity;
import com.hcmute.shopfee.enums.UsageConditionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "usage_condition")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsageConditionEntity {
    @Id
    @GenericGenerator(name = "usage_condition_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "usage_condition_id")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private UsageConditionType type;

//    @Column(name = "value")
//    private Integer remainAmount;

    @Column(name = "value")
    private Integer value;

    @ManyToOne
    @JoinColumn(name = "coupon_condition_id", nullable = false)
    @JsonBackReference
    private CouponConditionEntity couponCondition;
}
