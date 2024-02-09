package com.hcmute.shopfee.repository.database.coupon_used;

import com.hcmute.shopfee.entity.coupon_used.CouponUsedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponUsedRepository extends JpaRepository<CouponUsedEntity, String> {

    @Query(value = """
            select cu.id, cu.code, cu.coupon_id, cu.order_bill_id\s
            from coupon_used cu\s
            join order_bill ob on cu.order_bill_id ob.id\s
            where ob.user_id = ?1\s
            and cu.code = ?2\s
            """, nativeQuery = true)
    List<CouponUsedEntity> getCouponUsedByUserIdAndCode(String userId, String code);
}
