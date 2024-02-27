package com.hcmute.shopfee.entity.database.coupon;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.database.coupon_used.CouponUsedEntity;
import com.hcmute.shopfee.enums.CouponStatus;
import com.hcmute.shopfee.enums.CouponType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "coupon")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CouponEntity {
    @Id
    @GenericGenerator(name = "coupon_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "coupon_id")
    private String id;

    @Column(name = "code", nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_type", nullable = false)
    private CouponType couponType;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CouponStatus status;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isDeleted;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    // =================================================

    @OneToMany(mappedBy = "coupon")
    @JsonManagedReference
    private List<CouponUsedEntity> couponUsedList;

    @OneToMany(mappedBy = "coupon", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private List<CouponConditionEntity> conditionList;

    @OneToOne(mappedBy = "coupon", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private CouponRewardEntity couponReward;

//    @OneToOne(mappedBy = "coupon")
//    @JsonManagedReference
//    private CouponUsedEntity couponUsed;


}
