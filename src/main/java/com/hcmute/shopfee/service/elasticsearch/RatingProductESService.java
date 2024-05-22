package com.hcmute.shopfee.service.elasticsearch;

import com.hcmute.shopfee.entity.elasticsearch.RatingProductIndex;
import com.hcmute.shopfee.entity.sql.database.review.ProductReviewEntity;
import com.hcmute.shopfee.repository.database.review.ProductReviewRepository;
import com.hcmute.shopfee.repository.elasticsearch.RatingProductESRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingProductESService {
    private final ProductReviewRepository productReviewRepository;
    private final RatingProductESRepository ratingProductESRepository;
    public void syncProductIndexAndDatabase() {
        ratingProductESRepository.deleteAll();
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
        ratingProductESRepository.save(data);
    }
}
