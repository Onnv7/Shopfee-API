package com.hcmute.shopfee.repository.database.review;

import com.hcmute.shopfee.entity.database.identifier.UserProductReviewInteractionPK;
import com.hcmute.shopfee.entity.database.review.UserReviewInteractionEntity;
import com.hcmute.shopfee.enums.ReviewInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserReviewInteractionRepository extends JpaRepository<UserReviewInteractionEntity, UserProductReviewInteractionPK> {
    Optional<UserReviewInteractionEntity> findByUserIdAndProductReviewId(String userId, String productReviewId);
    List<UserReviewInteractionEntity> findByProductReviewIdAndInteraction(String productReviewId, ReviewInteraction interaction);
}
