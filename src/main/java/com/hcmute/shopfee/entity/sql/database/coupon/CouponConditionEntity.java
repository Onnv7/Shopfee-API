package com.hcmute.shopfee.entity.sql.database.coupon;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.CombinationConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.MinPurchaseConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.SubjectConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.UsageConditionEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import com.hcmute.shopfee.enums.ConditionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;

@Entity
@Table(name = "coupon_condition")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponConditionEntity {
    @Id
    @GenericGenerator(name = "coupon_condition_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "coupon_condition_id")
    @Column(length = 16)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ConditionType type;

    @ManyToOne()
    @JoinColumn(name = "coupon_id", nullable = false)
    @JsonBackReference
    private CouponEntity coupon;

    // =================================================================

    @OneToMany(mappedBy = "couponCondition", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private List<CombinationConditionEntity> combinationConditionList;

    @OneToOne(mappedBy = "couponCondition", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private MinPurchaseConditionEntity minPurchaseCondition;

    @OneToMany(mappedBy = "couponCondition", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private List<SubjectConditionEntity> subjectConditionList;

    @OneToMany(mappedBy = "couponCondition", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private List<UsageConditionEntity> usageConditionList;

}
