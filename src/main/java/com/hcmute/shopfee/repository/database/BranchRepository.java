package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.database.BranchEntity;
import com.hcmute.shopfee.enums.BranchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, String> {
    Page<BranchEntity> findByStatus(BranchStatus status, Pageable pageable);
    Optional<BranchEntity> findByIdAndStatus(Long id, BranchStatus status);
}
