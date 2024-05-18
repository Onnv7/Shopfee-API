package com.hcmute.shopfee.entity.sql.database.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import com.hcmute.shopfee.entity.sql.database.review.ProductReviewEntity;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;


@Entity
@Table(name = "order_item")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity {
    @Id
    @GenericGenerator(name = "order_item_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "order_item_id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private ProductEntity product;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "thumbnail_url", nullable = false)
    private String thumbnailUrl;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "order_bill_id", nullable = false)
    @JsonBackReference
    private OrderBillEntity orderBill;

    @OneToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "product_review_id")
    @JsonBackReference
    private ProductReviewEntity productReview;

    // =================================================
    @OneToMany(mappedBy = "orderItem", cascade = {CascadeType.PERSIST})
    @JsonManagedReference
    private List<ItemDetailEntity> itemDetailList;

}
