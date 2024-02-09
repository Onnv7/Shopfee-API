package com.hcmute.shopfee.repository.database.coupon;

import com.hcmute.shopfee.entity.coupon.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, String> {

    Optional<CouponEntity> findByIdAndIsDeletedFalse(String id);
    Optional<CouponEntity> findByCodeAndIsDeletedFalse(String code);
    List<CouponEntity> findByIsDeletedFalse();

    @Query(value = """
            select *
            from coupon c
            where c.is_deleted = 0
            and c.status = 'RELEASE'
            """, nativeQuery = true)
    List<CouponEntity> getReleaseCouponList();
}
