package com.hcmute.shopfee.entity.sql.database.coupon;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.sql.database.coupon.reward.MoneyRewardEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.reward.ProductRewardEntity;
import com.hcmute.shopfee.entity.sql.database.coupon_used.CouponUsedEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import com.hcmute.shopfee.enums.CouponRewardType;
import com.hcmute.shopfee.enums.CouponStatus;
import com.hcmute.shopfee.enums.CouponType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Collection;
import java.util.Date;
import java.util.List;

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
    @GenericGenerator(name = "coupon_id", type = RandomTimeGenerator.class)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false)
    private CouponRewardType rewardType;

    // =================================================

    @OneToMany(mappedBy = "coupon")
    @JsonManagedReference
    private List<CouponUsedEntity> couponUsedList;

    @OneToMany(mappedBy = "coupon", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private List<CouponConditionEntity> conditionList;

//    @OneToOne(mappedBy = "coupon", cascade = {CascadeType.PERSIST})
//    @JsonManagedReference
//    private CouponRewardEntity couponReward;


    @OneToOne(mappedBy = "coupon", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private MoneyRewardEntity moneyReward;

    @OneToMany(mappedBy = "coupon", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductRewardEntity> productRewardList;

    public void clearAndSetProductRewardList(Collection<ProductRewardEntity> newProductRewardNewList) {
        this.productRewardList.clear();
        this.productRewardList.addAll(newProductRewardNewList);
    }
}
