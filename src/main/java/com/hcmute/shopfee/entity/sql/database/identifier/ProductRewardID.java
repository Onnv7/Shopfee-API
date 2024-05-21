package com.hcmute.shopfee.entity.sql.database.identifier;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ProductRewardID {
    @JoinColumn(name = "coupon_id", nullable = false)
    private String couponId;
    @JoinColumn(name = "product_id", nullable = false)
    private String productId;
}
