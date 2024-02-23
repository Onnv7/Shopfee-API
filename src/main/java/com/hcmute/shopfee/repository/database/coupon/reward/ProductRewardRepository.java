package com.hcmute.shopfee.repository.database.coupon.reward;

import com.hcmute.shopfee.entity.database.coupon.reward.ProductRewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRewardRepository extends JpaRepository<ProductRewardEntity, String> {
}
