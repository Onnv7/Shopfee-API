package com.hcmute.shopfee.repository.database.product;

import com.hcmute.shopfee.entity.product.ProductEntity;
import com.hcmute.shopfee.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, String> {
    Optional<ProductEntity> findByNameAndIsDeletedFalse(String name);
    Optional<ProductEntity> findByIdAndIsDeletedFalse(String id);
    Optional<ProductEntity> findByStatusNotAndIsDeletedFalse(ProductStatus status);
    List<ProductEntity> findByCategory_IdAndIsDeletedFalse(String categoryId);
    Page<ProductEntity> findByStatusNotAndIsDeletedFalse(ProductStatus status, Pageable pageable);
    Optional<ProductEntity> findByIdAndCategory_IdAndIsDeletedFalse(String productId, String categoryId);

    @Query(value = """
            select *
            from product p
            where is_deleted = 0
            	and category_id regexp ?1
            	and status regexp ?2
            """, nativeQuery = true)
    Page<ProductEntity> getProductList(String categoryIdRegex, String productStatusRegex, Pageable pageable);

    @Query(value = """
            select count(*)
            from product p
            join order_item oi on p.id = oi.product_id
            where is_deleted = 0
            """, nativeQuery = true)
    long countOrderItem(String productId);
}
