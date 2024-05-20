package com.hcmute.shopfee.entity.sql.database.identifier;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class MoneyRewardReceivedID {
    @Column(name = "coupon_used_id", nullable = false)
    private String couponUsedId;
}
