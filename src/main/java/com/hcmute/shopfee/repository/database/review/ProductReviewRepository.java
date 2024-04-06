package com.hcmute.shopfee.repository.database.review;

import com.hcmute.shopfee.dto.sql.GetProductReviewStatisticDto;
import com.hcmute.shopfee.dto.sql.RatingSummaryQueryDto;
import com.hcmute.shopfee.entity.sql.database.review.ProductReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReviewEntity, String> {
    @Query(value = """
            select COALESCE(avg(pr.star), 0) as star, count(*) as quantity
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


    @Query(value = """
            SELECT stars.star_rating as starRating, COUNT(pr_data.star) AS count
            FROM (
                SELECT 1 AS star_rating UNION ALL
                SELECT 2 UNION ALL
                SELECT 3 UNION ALL
                SELECT 4 UNION ALL
                SELECT 5
            ) AS stars
            LEFT JOIN (
                SELECT pr.star
                FROM product p\s
                LEFT JOIN order_item oi ON p.id = oi.product_id\s
                LEFT JOIN product_review pr ON pr.id = oi.product_review_id\s
                WHERE p.id = ?1
            ) AS pr_data ON stars.star_rating = pr_data.star
            GROUP BY stars.star_rating
            order by stars.star_rating DESC\s
            """, nativeQuery = true)
    List<GetProductReviewStatisticDto> getProductReviewStatistic(String productId);
}
