package com.hcmute.shopfee.entity.sql.database.coupon.reward;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.MoneyRewardID;
import com.hcmute.shopfee.enums.MoneyRewardUnit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "money_reward")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoneyRewardEntity {
    @EmbeddedId
    private MoneyRewardID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private MoneyRewardUnit unit;

    @Column(name = "value", nullable = false)
    private Integer value;

    @OneToOne
    @MapsId("coupon_id")
    @JoinColumn(name = "coupon_id")
    @JsonBackReference
    private CouponEntity coupon;
}
