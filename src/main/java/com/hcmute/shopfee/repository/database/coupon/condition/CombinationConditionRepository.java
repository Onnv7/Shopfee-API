package com.hcmute.shopfee.repository.database.coupon.condition;

import com.hcmute.shopfee.entity.database.coupon.condition.CombinationConditionEntity;
import com.hcmute.shopfee.enums.CouponType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CombinationConditionRepository extends JpaRepository<CombinationConditionEntity, String> {


    @Query(value = """
            select cc.`type`\s
            from combination_condition cc\s
            join coupon_condition cc2 on cc.coupon_condition_id = cc2.id\s
            join coupon c on cc2.coupon_id = c.id\s
            where c.code = ?1 and c.status = 'RELEASED'
            """, nativeQuery = true)
    List<CouponType> getCombinationConditionByCouponCode(String couponCode);

    @Query(value = """
            select cc.`type`\s
            from combination_condition cc\s
            join coupon_condition cc2 on cc.coupon_condition_id = cc2.id\s
            join coupon c on cc2.coupon_id = c.id\s
            where (c.code = ?1 or c.code = ?2 or c.code = ?3) and c.status = 'RELEASED'
            """, nativeQuery = true)
    List<CouponType> getCombinationConditionByMoreCouponCode(String orderCouponCode, String shippingCouponCode, String productCouponCode);
}
