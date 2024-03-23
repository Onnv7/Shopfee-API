package com.hcmute.shopfee.repository.database.coupon.condition;

import com.hcmute.shopfee.entity.sql.database.coupon.condition.SubjectConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TargetObjectConditionRepository extends JpaRepository<SubjectConditionEntity, String> {
}
