package com.hcmute.shopfee.repository.database.coupon;

import com.hcmute.shopfee.entity.sql.database.coupon.CouponRewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRewardRepository extends JpaRepository<CouponRewardEntity, String> {
}
