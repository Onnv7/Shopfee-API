package com.hcmute.shopfee.repository.database.product;

import com.hcmute.shopfee.entity.sql.database.identifier.BranchProductId;
import com.hcmute.shopfee.entity.sql.database.product.BranchProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchProductRepository extends JpaRepository<BranchProductEntity, BranchProductId> {

}
