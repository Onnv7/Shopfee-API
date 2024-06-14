package com.hcmute.shopfee.entity.sql.database.identifier;

import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MoneyRewardID {
    @GenericGenerator(name = "coupon_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "coupon_id")
    @JoinColumn(name = "coupon_id", nullable = false)
    private String couponId;
}
