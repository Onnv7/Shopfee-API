package com.hcmute.shopfee.repository.database.review;

import com.hcmute.shopfee.dto.sql.RatingSummaryQueryDto;
import com.hcmute.shopfee.entity.database.review.ProductReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReviewEntity, String> {
    @Query(value = """
            select avg(pr.star) as star, count(*) as quantity
            from product_review pr\s
            join order_item oi on oi.product_review_id = pr.id\s
            where oi.product_id = ?1
            """, nativeQuery = true)
    RatingSummaryQueryDto getRatingSummary(String productId);

    @Query(value = """
            select pr.id, pr.content, pr.created_at, pr.star
            from order_item oi
            join product_review pr on oi.product_review_id = pr.id
            where oi.product_id = ?1
            """, nativeQuery = true)
    Page<ProductReviewEntity> getProductReviewByProductId(String productId, Pageable pageable);

}
