package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.database.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, String> {
}
