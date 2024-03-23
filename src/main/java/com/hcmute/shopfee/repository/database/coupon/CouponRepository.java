package com.hcmute.shopfee.repository.database.coupon;

import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import com.hcmute.shopfee.enums.CouponStatus;
import com.hcmute.shopfee.enums.CouponType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, String> {
    Optional<CouponEntity> findByIdAndCouponTypeAndIsDeletedFalse(String id, CouponType couponType);

    Optional<CouponEntity> findByIdAndIsDeletedFalse(String id);
    Optional<CouponEntity> findByCodeAndIsDeletedFalse(String code);
    Optional<CouponEntity> findByCodeAndStatusAndIsDeletedFalse(String code, CouponStatus status);
    List<CouponEntity> findByIsDeletedFalse();

    @Query(value = """
            select *
            from coupon c
            where c.is_deleted = 0
            and c.status = 'RELEASED'
            """, nativeQuery = true)
    List<CouponEntity> getReleaseCouponList();
    List<CouponEntity> findByStatusAndCouponType(CouponStatus status, CouponType couponType);
}
