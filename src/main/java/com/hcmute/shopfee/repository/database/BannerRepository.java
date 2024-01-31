package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.BannerEntity;
import com.hcmute.shopfee.enums.BannerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BannerRepository extends JpaRepository<BannerEntity, String> {
    List<BannerEntity> findByIsDeletedFalseAndStatus(BannerStatus status);
    Optional<BannerEntity> findByIdAndIsDeletedFalse(String bannerId);
    List<BannerEntity> findByIsDeletedFalse();

}
