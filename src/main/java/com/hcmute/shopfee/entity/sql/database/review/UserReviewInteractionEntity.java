package com.hcmute.shopfee.entity.sql.database.review;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.identifier.UserProductReviewInteractionPK;
import com.hcmute.shopfee.enums.ReviewInteraction;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "user_review_interaction")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
//@IdClass(UserProductReviewInteractionPK.class)
public class UserReviewInteractionEntity {
    @EmbeddedId
    private UserProductReviewInteractionPK id;

    @Column(name = "user_id", nullable = false, insertable=false, updatable=false)
    private String userId;

    @ManyToOne
    @JoinColumn(name = "product_review_id", insertable=false, updatable=false)
    @JsonBackReference
    private ProductReviewEntity productReview;

    @Enumerated(EnumType.STRING)
    @Column(name = "interaction", nullable = false)
    private ReviewInteraction interaction;
}
