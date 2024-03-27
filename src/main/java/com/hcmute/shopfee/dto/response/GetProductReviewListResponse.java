package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmute.shopfee.entity.sql.database.UserEntity;
import com.hcmute.shopfee.entity.sql.database.review.ProductReviewEntity;
import com.hcmute.shopfee.enums.ReviewInteraction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetProductReviewListResponse {
    private Integer totalPage;
    private List<ProductReview> productReviewList;

    @Data
    public static class ProductReview {
        private String id;
        private Integer star;
        private String content;
        private String avatarUrl;
        private String reviewerName;
        private Integer dislikeQuantity;
        private Integer likeQuantity;
        private Date createdAt;
        private ReviewInteraction interaction;

    }
    public static ProductReview fromProductReviewEntity(ProductReviewEntity entity, int dislikeQuantity, int likeQuantity, ReviewInteraction interaction) {
        ProductReview data = new ProductReview();
        UserEntity user = entity.getOrderItem().getOrderBill().getUser();
        data.setId(entity.getId());
        data.setStar(entity.getStar());
        data.setContent(entity.getContent());
        data.setAvatarUrl(user.getAvatarUrl());
        data.setReviewerName(user.getFullName());
        data.setDislikeQuantity(dislikeQuantity);
        data.setLikeQuantity(likeQuantity);
        data.setCreatedAt(entity.getCreatedAt());
        data.setInteraction(interaction);
        return data;
    }
}
