package com.hcmute.shopfee.entity.sql.database.identifier;

import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MoneyRewardID  {
    private CouponEntity coupon;
//    @JoinColumn(name = "coupon_id", nullable = false)
//    private String couponId;
//
//    public String getCouponId() {
//        return couponId;
//    }
//
//    public void setCouponId(String couponId) {
//        this.couponId = couponId;
//    }
}
