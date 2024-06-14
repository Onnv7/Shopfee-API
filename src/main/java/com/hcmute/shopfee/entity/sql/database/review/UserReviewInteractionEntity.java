package com.hcmute.shopfee.entity.sql.database.review;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.UserEntity;
import com.hcmute.shopfee.entity.sql.database.identifier.UserProductReviewInteractionID;
import com.hcmute.shopfee.enums.ReviewInteraction;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "user_review_interaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserReviewInteractionEntity {
    @EmbeddedId
    private UserProductReviewInteractionID id = new UserProductReviewInteractionID();

    @MapsId("userId")
    @ManyToOne
    @JsonBackReference
    private UserEntity user;

    @MapsId("productReviewId")
    @ManyToOne
    @JsonBackReference
    private ProductReviewEntity productReview;

    @Enumerated(EnumType.STRING)
    @Column(name = "interaction", nullable = false)
    private ReviewInteraction interaction;
}
