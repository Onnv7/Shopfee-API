package com.hcmute.shopfee.repository.database.coupon_used;

import com.hcmute.shopfee.entity.sql.database.coupon_used.CouponUsedEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponUsedRepository extends JpaRepository<CouponUsedEntity, String> {

    @Query(value = """
            select cu.*, cu.id as fakeColumns
            from coupon_used cu\s
            join order_bill ob on cu.order_bill_id = ob.id\s
            where ob.user_id = ?1\s
            and cu.coupon_id = ?2\s
            """, nativeQuery = true)
    Optional<CouponUsedEntity> getCouponUsedByUserIdAndCode(String userId, String couponId);



//    @Transactional
//    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query(value = """
          select count(*)
          from coupon_used cu
          where cu.coupon_id = ?1
            """, nativeQuery = true)
//    @Query("SELECT COUNT(cu) FROM CouponUsedEntity cu WHERE cu.coupon.id = ?1")
    int getUsedCouponCount(String couponId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cu FROM CouponUsedEntity cu JOIN cu.coupon c WHERE c.code = ?1")
    List<CouponUsedEntity> getWithLockingByCouponCode(String couponCode);
}
