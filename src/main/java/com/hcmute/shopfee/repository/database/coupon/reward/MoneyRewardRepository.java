package com.hcmute.shopfee.repository.database.coupon.reward;

import com.hcmute.shopfee.entity.sql.database.coupon.reward.MoneyRewardEntity;
import com.hcmute.shopfee.repository.database.coupon.CouponRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoneyRewardRepository extends CouponRepository {
}
