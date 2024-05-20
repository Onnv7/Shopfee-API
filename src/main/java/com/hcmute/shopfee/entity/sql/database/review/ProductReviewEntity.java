package com.hcmute.shopfee.entity.sql.database.review;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.entity.sql.database.UserEntity;
import com.hcmute.shopfee.entity.sql.database.order.OrderItemEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;

import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;

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
    @GenericGenerator(name = "product_review_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "product_review_id")
    private String id;

    @Column(name = "star", nullable = false, columnDefinition = "INT CHECK(star > 0 and star <= 5)")
    private Integer star;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    // =================================================
    @OneToMany(mappedBy = "productReview")
    @JsonManagedReference
    private List<UserReviewInteractionEntity> userReviewInteractionList;

    @OneToOne(mappedBy = "productReview")
    @JsonManagedReference
    private OrderItemEntity orderItem;
}
