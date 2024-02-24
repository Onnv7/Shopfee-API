package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
        private Integer star;
        private String content;
        private String reviewerName;
        private Integer dislikeQuantity;
        private Integer likeQuantity;
        private Date createdAt;
        private ReviewInteraction interaction;
    }
}
