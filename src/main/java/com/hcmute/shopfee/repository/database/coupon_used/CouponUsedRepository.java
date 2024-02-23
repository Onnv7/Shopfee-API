package com.hcmute.shopfee.repository.database.coupon_used;

import com.hcmute.shopfee.entity.database.coupon_used.CouponUsedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponUsedRepository extends JpaRepository<CouponUsedEntity, String> {

    @Query(value = """
            select cu.id, cu.code, cu.coupon_id, cu.order_bill_id\s
            from coupon_used cu\s
            join order_bill ob on cu.order_bill_id ob.id\s
            where ob.user_id = ?1\s
            and cu.coupon_id = ?2\s
            """, nativeQuery = true)
    Optional<CouponUsedEntity> getCouponUsedByUserIdAndCode(String userId, String couponId);

    @Query(value = """
          select count(*)
          from coupon_used cu
          where cu.coupon_id = ?1
            """, nativeQuery = true)
    int getUsedCouponCount(String couponId);
}
