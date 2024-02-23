package com.hcmute.shopfee.repository.database.coupon.condition;

import com.hcmute.shopfee.entity.database.coupon.condition.MinPurchaseConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MinPurchaseConditionRepository extends JpaRepository<MinPurchaseConditionEntity, String> {
}
