package com.hcmute.shopfee.entity.sql.database.identifier;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ProductRewardID {
    @JoinColumn(name = "coupon_id", nullable = false)
    private String couponId;
    @JoinColumn(name = "product_id", nullable = false)
    private String productId;
}
