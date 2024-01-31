package com.hcmute.shopfee.repository.database.coupon.condition;

import com.hcmute.shopfee.entity.coupon.condition.TargetObjectConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TargetObjectConditionRepository extends JpaRepository<TargetObjectConditionEntity, String> {
}
