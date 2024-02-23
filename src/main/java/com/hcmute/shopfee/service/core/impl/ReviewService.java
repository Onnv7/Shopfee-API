package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.CreateReviewRequest;
import com.hcmute.shopfee.entity.database.ProductReviewEntity;
import com.hcmute.shopfee.entity.database.order.OrderItemEntity;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.OrderItemRepository;
import com.hcmute.shopfee.service.core.IReviewService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    private final ModelMapperService modelMapperService;
    private final OrderItemRepository orderItemRepository;

    @Override
    public void createReview(CreateReviewRequest body) {
        ProductReviewEntity productReviewEntity = modelMapperService.mapClass(body, ProductReviewEntity.class);
        OrderItemEntity orderItemEntity = orderItemRepository.findById(body.getOrderItemId())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + body.getOrderItemId()));
        orderItemEntity.setProductReview(productReviewEntity);
        orderItemRepository.save(orderItemEntity);
    }
}