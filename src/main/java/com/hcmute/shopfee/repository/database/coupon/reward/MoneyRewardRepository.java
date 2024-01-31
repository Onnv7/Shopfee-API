package com.hcmute.shopfee.repository.database.coupon.reward;

import com.hcmute.shopfee.entity.coupon.reward.MoneyRewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoneyRewardRepository extends JpaRepository<MoneyRewardEntity, String> {
}
