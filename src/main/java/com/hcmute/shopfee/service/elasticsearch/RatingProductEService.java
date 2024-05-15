package com.hcmute.shopfee.service.elasticsearch;

import com.hcmute.shopfee.entity.elasticsearch.RatingProductIndex;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.entity.sql.database.review.ProductReviewEntity;
import com.hcmute.shopfee.repository.database.review.ProductReviewRepository;
import com.hcmute.shopfee.repository.elasticsearch.RatingProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingProductEService {
    private final ProductReviewRepository productReviewRepository;
    private final RatingProductRepository ratingProductRepository;
    public void syncProductIndexAndDatabase() {
        ratingProductRepository.deleteAll();
        List<ProductReviewEntity> reviewList = productReviewRepository.findAll();
        for (ProductReviewEntity productReviewEntity : reviewList) {
            createRatingProductIndex(productReviewEntity);
        }
    }

    public void createRatingProductIndex(ProductReviewEntity productReviewEntity) {
        RatingProductIndex data = RatingProductIndex.builder()
                .rating(productReviewEntity.getStar())
                .userId(productReviewEntity.getCreatedBy())
                .productId(productReviewEntity.getOrderItem().getProduct().getId())
                .build();
        ratingProductRepository.save(data);
    }
}
