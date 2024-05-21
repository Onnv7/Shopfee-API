package com.hcmute.shopfee.entity.sql.database.identifier;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MoneyRewardReceivedID {
    @Column(name = "coupon_used_id", nullable = false)
    private String couponUsedId;
}
