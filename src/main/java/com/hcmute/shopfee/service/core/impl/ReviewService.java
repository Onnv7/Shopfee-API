package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.CreateReviewRequest;
import com.hcmute.shopfee.dto.request.InteractProductReviewRequest;
import com.hcmute.shopfee.dto.response.GetProductReviewListResponse;
import com.hcmute.shopfee.entity.database.identifier.UserProductReviewInteractionPK;
import com.hcmute.shopfee.entity.database.review.ProductReviewEntity;
import com.hcmute.shopfee.entity.database.order.OrderItemEntity;
import com.hcmute.shopfee.entity.database.review.UserReviewInteractionEntity;
import com.hcmute.shopfee.enums.ReviewInteraction;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.OrderItemRepository;
import com.hcmute.shopfee.repository.database.review.ProductReviewRepository;
import com.hcmute.shopfee.repository.database.review.UserReviewInteractionRepository;
import com.hcmute.shopfee.service.core.IReviewService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    private final ModelMapperService modelMapperService;
    private final OrderItemRepository orderItemRepository;
    private final UserReviewInteractionRepository userReviewInteractionRepository;
    private final ProductReviewRepository productReviewRepository;

    @Override
    public void createProductReview(CreateReviewRequest body) {
        ProductReviewEntity productReviewEntity = modelMapperService.mapClass(body, ProductReviewEntity.class);
        OrderItemEntity orderItemEntity = orderItemRepository.findById(body.getOrderItemId())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + body.getOrderItemId()));
        SecurityUtils.checkUserId(orderItemEntity.getOrderBill().getUser().getId());
        orderItemEntity.setProductReview(productReviewEntity);
        orderItemRepository.save(orderItemEntity);
    }

    @Override
    public void createProductReviewInteraction(String productReviewId, InteractProductReviewRequest body) {
        SecurityUtils.checkUserId(body.getUserId());
        ProductReviewEntity productReviewEntity = productReviewRepository.findById(productReviewId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + productReviewId));

        UserReviewInteractionEntity entity = userReviewInteractionRepository.findByUserIdAndProductReviewId(body.getUserId(), productReviewId)
                .orElse(null);
        if (entity == null) {
            entity = new UserReviewInteractionEntity();
            entity.setId(new UserProductReviewInteractionPK(body.getUserId(), productReviewId));
            entity.setInteraction(body.getInteraction());
            entity.setUserId(body.getUserId());
            entity.setProductReview(productReviewEntity);
            userReviewInteractionRepository.save(entity);
        } else {
            if(entity.getInteraction() == body.getInteraction()) {
                userReviewInteractionRepository.delete(entity);
            } else {
                ReviewInteraction interaction = entity.getInteraction() == ReviewInteraction.DISLIKE ? ReviewInteraction.LIKE : ReviewInteraction.DISLIKE;
                entity.setInteraction(interaction);
                userReviewInteractionRepository.save(entity);
            }
        }
    }

    @Override
    public GetProductReviewListResponse getProductReviewListByProductId(String productId, int page, int size) {
        String userId = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page - 1, size);
        GetProductReviewListResponse data = new GetProductReviewListResponse();

        List<GetProductReviewListResponse.ProductReview> productReviewList = new ArrayList<>();
        data.setProductReviewList(productReviewList);

        Page<ProductReviewEntity> productReviewEntityList = productReviewRepository.getProductReviewByProductId(productId, pageable);
        data.setTotalPage(productReviewEntityList.getTotalPages());

        for(ProductReviewEntity productReviewEntity : productReviewEntityList.getContent()) {
            GetProductReviewListResponse.ProductReview productReview = new GetProductReviewListResponse.ProductReview();
            productReview.setReviewerName(productReviewEntity.getOrderItem().getOrderBill().getUser().getFullName());
            productReview.setStar(productReviewEntity.getStar());
            productReview.setContent(productReviewEntity.getContent());
            productReview.setCreatedAt(productReviewEntity.getCreatedAt());

            List<UserReviewInteractionEntity> userDislikeList = userReviewInteractionRepository.findByProductReviewIdAndInteraction(productReviewEntity.getId(), ReviewInteraction.DISLIKE);
            productReview.setDislikeQuantity(userDislikeList.size());

            List<UserReviewInteractionEntity> userLikeList = userReviewInteractionRepository.findByProductReviewIdAndInteraction(productReviewEntity.getId(), ReviewInteraction.LIKE);
            productReview.setLikeQuantity(userLikeList.size());

            if(userId != null) {
                UserReviewInteractionEntity userDisliked = userDislikeList.stream().filter(it -> Objects.equals(it.getUserId(), userId)).findFirst().orElse(null);
                UserReviewInteractionEntity userLiked = userLikeList.stream().filter(it -> Objects.equals(it.getUserId(), userId)).findFirst().orElse(null);
                if(userDisliked != null) {
                    productReview.setInteraction(ReviewInteraction.DISLIKE);
                } else if(userLiked != null) {
                    productReview.setInteraction(ReviewInteraction.LIKE);
                }
            }
            productReviewList.add(productReview);
        }
        return data;
    }
}
