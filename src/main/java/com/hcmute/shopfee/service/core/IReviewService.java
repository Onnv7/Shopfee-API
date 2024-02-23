package com.hcmute.shopfee.service.core;


import com.hcmute.shopfee.dto.request.CreateReviewRequest;

public interface IReviewService {
    void createReview(CreateReviewRequest body);
}
