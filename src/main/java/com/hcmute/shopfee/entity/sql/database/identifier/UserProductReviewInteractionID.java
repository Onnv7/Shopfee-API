package com.hcmute.shopfee.entity.sql.database.identifier;

import jakarta.persistence.Column;
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
public class UserProductReviewInteractionID implements Serializable {
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(name = "product_review_id", nullable = false)
    private String productReviewId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProductReviewInteractionID that = (UserProductReviewInteractionID) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(productReviewId, that.productReviewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, productReviewId);
    }
}
