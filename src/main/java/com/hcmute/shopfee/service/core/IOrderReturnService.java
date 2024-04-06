package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.CreateOrderReturnRequest;
import com.hcmute.shopfee.enums.AnswerStatus;

public interface IOrderReturnService {
    void createOrderRefundRequest(CreateOrderReturnRequest body, String orderId);
    void processOrderRefundRequest(AnswerStatus status, String orderId);
}
