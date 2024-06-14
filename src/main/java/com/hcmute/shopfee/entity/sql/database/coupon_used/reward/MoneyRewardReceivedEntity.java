package com.hcmute.shopfee.entity.sql.database.coupon_used.reward;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.coupon_used.CouponUsedEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.MoneyRewardReceivedID;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import com.hcmute.shopfee.enums.MoneyRewardUnit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "money_reward_received")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoneyRewardReceivedEntity {
//    @Id
//    @GenericGenerator(name = "money_reward_received_id", type = RandomTimeGenerator.class)
//    @GeneratedValue(generator = "money_reward_received_id")
//    private String id;


    public MoneyRewardReceivedEntity(MoneyRewardUnit unit, Integer value, CouponUsedEntity couponUsed) {
        this.unit = unit;
        this.value = value;
        this.couponUsed = couponUsed;
    }

    @EmbeddedId
    private MoneyRewardReceivedID id = new MoneyRewardReceivedID();

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private MoneyRewardUnit unit;

    @Column(name = "value", nullable = false)
    private Integer value;

    @OneToOne
    @MapsId("couponUsedId")
//    @JoinColumn(name = "coupon_used_id", referencedColumnName = "coupon_id")
    @JsonBackReference
    private CouponUsedEntity couponUsed;
}
