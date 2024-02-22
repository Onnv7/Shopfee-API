package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.ProductReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReviewEntity, String> {
}
