package com.hcmute.shopfee.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.order.ItemToppingEntity;
import com.hcmute.shopfee.entity.order.OrderItemEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "product_review")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ProductReviewEntity {
    @Id
    @GenericGenerator(name = "product_review_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "product_review_id")
    private String id;

    @Column(name = "star", nullable = false)
    private Integer star;

    @Column(name = "description", nullable = false)
    private String description;


    @OneToOne(mappedBy = "productReview")
    @JsonManagedReference
    private OrderItemEntity orderItem;

}
