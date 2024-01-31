package com.hcmute.shopfee.repository.database.coupon.condition;

import com.hcmute.shopfee.entity.coupon.condition.ApplicableCustomerConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicableCustomerConditionRepository extends JpaRepository<ApplicableCustomerConditionEntity, String> {
}
