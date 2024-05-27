package com.hcmute.shopfee.service.core;


import com.hcmute.shopfee.payload.request.CreateReviewRequest;
import com.hcmute.shopfee.payload.request.InteractProductReviewRequest;
import com.hcmute.shopfee.payload.response.GetProductReviewListResponse;
import com.hcmute.shopfee.payload.response.GetProductReviewStatisticResponse;
import com.hcmute.shopfee.enums.param.ReviewSortType;

public interface IReviewService {
    void createProductReview(CreateReviewRequest body);
    void createProductReviewInteraction(String productReviewId, InteractProductReviewRequest body);
    GetProductReviewListResponse getProductReviewListByProductId(String productId, int page, int size, ReviewSortType sortType);
    GetProductReviewStatisticResponse getProductReviewStatistic(String productId);
}
