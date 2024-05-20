package com.hcmute.shopfee.entity.sql.database.coupon.reward;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.ProductRewardID;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_reward")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRewardEntity {
    @EmbeddedId
    private ProductRewardID id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @MapsId("productId")
    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private ProductEntity product;


    @Column(name = "product_size")
    private String productSize;

    @Column(name = "quantity", nullable = false)
    private Short quantity;

    @ManyToOne
    @MapsId("couponId")
    @JoinColumn(name = "coupon_id", nullable = false)
    @JsonBackReference
    private CouponEntity coupon;
}
