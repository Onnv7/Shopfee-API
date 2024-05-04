package com.hcmute.shopfee.service.core;


import com.hcmute.shopfee.dto.request.CreateReviewRequest;
import com.hcmute.shopfee.dto.request.InteractProductReviewRequest;
import com.hcmute.shopfee.dto.response.GetProductReviewListResponse;
import com.hcmute.shopfee.dto.response.GetProductReviewStatisticResponse;
import com.hcmute.shopfee.enums.ReviewInteraction;
import com.hcmute.shopfee.enums.param.ReviewSortType;

import java.util.List;

public interface IReviewService {
    void createProductReview(CreateReviewRequest body);
    void createProductReviewInteraction(String productReviewId, InteractProductReviewRequest body);
    GetProductReviewListResponse getProductReviewListByProductId(String productId, int page, int size, ReviewSortType sortType);
    GetProductReviewStatisticResponse getProductReviewStatistic(String productId);
}
