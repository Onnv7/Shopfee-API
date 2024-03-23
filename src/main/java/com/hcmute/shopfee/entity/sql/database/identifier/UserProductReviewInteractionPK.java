package com.hcmute.shopfee.entity.sql.database.identifier;

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
public class UserProductReviewInteractionPK implements Serializable {
    private String user_id;
    private String product_review_id ;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProductReviewInteractionPK that = (UserProductReviewInteractionPK) o;
        return Objects.equals(user_id, that.user_id) &&
                Objects.equals(product_review_id, that.product_review_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, product_review_id);
    }
}
