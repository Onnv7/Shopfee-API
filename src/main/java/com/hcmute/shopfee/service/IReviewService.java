package com.hcmute.shopfee.service;


import com.hcmute.shopfee.dto.request.CreateReviewRequest;

public interface IReviewService {
    void createReview(CreateReviewRequest body);
}
