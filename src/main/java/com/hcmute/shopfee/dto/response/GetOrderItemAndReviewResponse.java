package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.sql.database.order.OrderItemEntity;
import com.hcmute.shopfee.entity.sql.database.review.ProductReviewEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class GetOrderItemAndReviewResponse {
    private String name;
    private String thumbnailUrl;
    private ItemReview review;

    @Data
    private static class ItemReview {
        private String content;
        private Integer star;
        private Date createdAt;
    }
    private static GetOrderItemAndReviewResponse fromOrderItemEntity(OrderItemEntity entity) {
        GetOrderItemAndReviewResponse data = new GetOrderItemAndReviewResponse();
        data.setName(entity.getName());
        data.setThumbnailUrl(entity.getThumbnailUrl());
        ProductReviewEntity productReviewEntity = entity.getProductReview();
        if (productReviewEntity != null) {
            ItemReview review = new ItemReview();
            review.setContent(productReviewEntity.getContent());
            review.setStar(productReviewEntity.getStar());
            review.setCreatedAt(productReviewEntity.getCreatedAt());
            data.setReview(review);
        }
        return data;
    }

    public static List<GetOrderItemAndReviewResponse> fromOrderItemEntityList(List<OrderItemEntity> entityList) {
        List<GetOrderItemAndReviewResponse> data = new ArrayList<>();
        for (OrderItemEntity entity: entityList) {
            data.add(fromOrderItemEntity(entity));
        }
        return data;
    }
}
