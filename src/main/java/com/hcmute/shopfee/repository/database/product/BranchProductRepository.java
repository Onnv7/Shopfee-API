package com.hcmute.shopfee.repository.database.product;

import com.hcmute.shopfee.entity.sql.database.identifier.BranchProductId;
import com.hcmute.shopfee.entity.sql.database.product.BranchProductEntity;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchProductRepository extends JpaRepository<BranchProductEntity, BranchProductId> {
    @Query(value = """
            select bp.*
            from product p
            join branch_product bp on p.id = bp.product_id
            where bp.branch_id = ?1
                and p.status = 'ACTIVE'
            """, nativeQuery = true)
    List<BranchProductEntity> getBranchProductActive(String branchId);
    @Query(value = """
            select bp.*
            from product p
            join branch_product bp on p.id = bp.product_id
            join branch b on b.id = bp.branch_id
            where p.id = ?1
                and bp.status regexp ?2
                and b.name regexp ?3
                and b.id regexp ?3
            """, nativeQuery = true)
    Page<BranchProductEntity> getBranchProductListByProduct(String productId, String branchProductStatusRegex, String keyRegex, Pageable pageable);


    @Query(value = """
            select bp.*
            from branch_product bp
            join product p on p.id = bp.product_id
            where bp.id = ?1
                and p.status = 'ACTIVE'
                and bp.status = 'AVAILABLE'
                and bp.branch_id = ?2
            """, nativeQuery = true)
    Optional<BranchProductEntity> getProductBranchAvailable(String productId, String branchId);
}
