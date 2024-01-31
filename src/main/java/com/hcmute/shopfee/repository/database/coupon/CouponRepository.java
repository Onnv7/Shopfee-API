package com.hcmute.shopfee.repository.database.coupon;

import com.hcmute.shopfee.entity.coupon.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, String> {
}
