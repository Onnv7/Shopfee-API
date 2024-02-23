package com.hcmute.shopfee.repository.database.coupon.condition;

import com.hcmute.shopfee.entity.database.coupon.condition.CombinationConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CombinationConditionRepository extends JpaRepository<CombinationConditionEntity, String> {
}
