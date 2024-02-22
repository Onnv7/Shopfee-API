package com.hcmute.shopfee.entity.coupon.condition;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.coupon.CouponConditionEntity;
import com.hcmute.shopfee.enums.TargetType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "target_object_condition")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectConditionEntity {
    @Id
    @GenericGenerator(name = "target_object_condition_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "target_object_condition_id")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TargetType type;

    @Column(name = "value", nullable = false)
    private String value;

    @ManyToOne
    @JoinColumn(name = "coupon_condition_id", nullable = false)
    @JsonBackReference
    private CouponConditionEntity couponCondition;
}
