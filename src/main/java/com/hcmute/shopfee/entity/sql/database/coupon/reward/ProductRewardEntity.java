package com.hcmute.shopfee.entity.sql.database.coupon.reward;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.ProductRewardID;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_reward")
@Getter
@Setter
@NoArgsConstructor
public class ProductRewardEntity {
    public ProductRewardEntity( String productName, ProductEntity product, String productSize, Short quantity, CouponEntity coupon) {
        this.productName = productName;
        this.product = product;
        this.productSize = productSize;
        this.quantity = quantity;
        this.coupon = coupon;
    }

    @EmbeddedId
    private ProductRewardID id = new ProductRewardID();

    @Column(name = "product_name", nullable = false)
    private String productName;

    @MapsId("productId")
    @ManyToOne
//    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private ProductEntity product;


    @Column(name = "product_size")
    private String productSize;

    @Column(name = "quantity", nullable = false)
    private Short quantity;

    @ManyToOne()
    @MapsId("couponId")
//    @JoinColumn(name = "coupon_id", nullable = false)
    @JsonBackReference
    private CouponEntity coupon;
}
