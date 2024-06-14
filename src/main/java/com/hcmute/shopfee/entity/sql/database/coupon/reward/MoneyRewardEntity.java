package com.hcmute.shopfee.entity.sql.database.coupon.reward;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.MoneyRewardID;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import com.hcmute.shopfee.enums.MoneyRewardUnit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "money_reward")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoneyRewardEntity {

    @Id
    @GenericGenerator(name = "coupon_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "coupon_id")
    private String id;
//    @EmbeddedId
//    public MoneyRewardID id;

//    @Column(name = "coupon_id")
//    private String couponId;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private MoneyRewardUnit unit;

    @Column(name = "value", nullable = false)
    private Integer value;

    @OneToOne
    @JoinColumn(name = "coupon_id")
    @JsonBackReference()
    private CouponEntity coupon;
}
