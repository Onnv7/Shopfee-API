package com.hcmute.shopfee.repository.database.coupon;

import com.hcmute.shopfee.entity.sql.database.coupon.CouponConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponConditionRepository extends JpaRepository<CouponConditionEntity, String> {
}
