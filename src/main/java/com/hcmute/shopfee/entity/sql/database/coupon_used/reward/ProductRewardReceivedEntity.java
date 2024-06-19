package com.hcmute.shopfee.entity.sql.database.coupon_used.reward;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.coupon_used.CouponUsedEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.ProductRewardReceivedID;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "product_reward_received")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRewardReceivedEntity {

    public ProductRewardReceivedEntity(ProductEntity product, Short quantity, String productName, String productSize, CouponUsedEntity couponUsed) {
        this.product = product;
        this.quantity = quantity;
        this.productName = productName;
        this.productSize = productSize;
        this.couponUsed = couponUsed;
    }

    @EmbeddedId
    private ProductRewardReceivedID id = new ProductRewardReceivedID();

    @MapsId("productId")
    @ManyToOne
    @JsonBackReference
    private ProductEntity product;

    @Column(name = "quantity", nullable = false)
    private Short quantity;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_size", nullable = false)
    private String productSize;

    @ManyToOne
    @MapsId("couponUsedId")
    @JsonBackReference
    private CouponUsedEntity couponUsed;
}
