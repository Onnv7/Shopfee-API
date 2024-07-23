package com.hcmute.shopfee.entity.sql.database.coupon.condition;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponConditionEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "subject_condition")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectConditionEntity {
    @Id
    @GenericGenerator(name = "subject_condition_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "subject_condition_id")
    @Column(length = 16)
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
