package com.hcmute.shopfee.entity.database.review;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.database.order.OrderItemEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
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

    @Column(name = "content", nullable = false)
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;


//    @Temporal(TemporalType.TIMESTAMP)
//    @LastModifiedDate
//    @Column(name = "update_at")
//    private Date updateAt;

    // =================================================
    @OneToMany(mappedBy = "productReview")
    @JsonManagedReference
    private List<UserReviewInteractionEntity> userReviewInteractionList;

    @OneToOne(mappedBy = "productReview")
    @JsonManagedReference
    private OrderItemEntity orderItem;
}
