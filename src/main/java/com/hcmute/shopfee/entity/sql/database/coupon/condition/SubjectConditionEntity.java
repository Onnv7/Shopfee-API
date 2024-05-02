package com.hcmute.shopfee.entity.sql.database.coupon.condition;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponConditionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "subject_condition")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectConditionEntity {
    @Id
    @GenericGenerator(name = "subject_condition_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "subject_condition_id")
    private String id;

    @Column(name = "object_id", nullable = false)
    private String objectId;

    @Column(name = "object_name", nullable = false)
    private String productName;

    @Column(name = "value", nullable = false)
    private Integer value;

    @ManyToOne
    @JoinColumn(name = "coupon_condition_id", nullable = false)
    @JsonBackReference
    private CouponConditionEntity couponCondition;
}
