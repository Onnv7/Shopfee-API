package com.hcmute.shopfee.entity.sql.database.identifier;

import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ProductRewardReceivedID {
    @JoinColumn(name = "coupon_used_id", nullable = false)
    private String couponUsedId;
    @JoinColumn(name = "product_id", nullable = false)
    private String productId;
}
