package com.hcmute.shopfee.dto.common;

import lombok.Data;

@Data
public class RatingEdge {
    private String userId;
    private String productId;
    private Integer rating;

    public RatingEdge(String userId, String productId, Integer rating) {
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
    }
}
