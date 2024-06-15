package com.hcmute.shopfee.repository.database.coupon;

import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import com.hcmute.shopfee.enums.CouponStatus;
import com.hcmute.shopfee.enums.CouponType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, String> {
    Optional<CouponEntity> findByIdAndCouponTypeAndIsDeletedFalse(String id, CouponType couponType);

    Optional<CouponEntity> findByIdAndIsDeletedFalse(String id);
    Optional<CouponEntity> findByCodeAndIsDeletedFalse(String code);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<CouponEntity> findByCodeAndStatusAndIsDeletedFalse(String code, CouponStatus status);
    Optional<CouponEntity> findByCodeAndStatusAndCouponTypeAndIsDeletedFalse(String code,CouponStatus status, CouponType couponType);
    List<CouponEntity> findByIsDeletedFalse();

    @Query(value = """
            select *
            from coupon c
            where c.is_deleted = 0
            and c.status = 'RELEASED'
            limit ?1
            """, nativeQuery = true)
    List<CouponEntity> getReleaseCouponList(int limit);
    List<CouponEntity> findByStatusAndCouponTypeAndIsDeletedFalse(CouponStatus status, CouponType couponType);
}
