package com.hcmute.shopfee.entity.sql.database.identifier;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserProductReviewInteractionID {
    @JoinColumn(name = "user_id", updatable=false)
    private String userId;
    @JoinColumn(name = "product_review_id", updatable=false)
    private String productReviewId;
}
