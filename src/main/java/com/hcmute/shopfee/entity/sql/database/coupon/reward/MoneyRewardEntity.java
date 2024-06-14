package com.hcmute.shopfee.entity.sql.database.coupon.reward;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.MoneyRewardID;
import com.hcmute.shopfee.enums.MoneyRewardUnit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "money_reward")
//@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoneyRewardEntity  {
    public MoneyRewardEntity( MoneyRewardUnit unit, Integer value, CouponEntity coupon) {
        this.unit = unit;
        this.value = value;
        this.coupon = coupon;
    }

    @EmbeddedId
    private MoneyRewardID id = new MoneyRewardID();


//    public MoneyRewardEntity(MoneyRewardUnit unit, Integer value) {
//        super();
//        this.unit = unit;
//        this.value = value;
//    }

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private MoneyRewardUnit unit;

    @Column(name = "value", nullable = false)
    private Integer value;

    @OneToOne(cascade = CascadeType.MERGE)
    @MapsId("couponId")
//    @JoinColumn(name = "coupon_id")
    @JsonBackReference
    private CouponEntity coupon;
}
