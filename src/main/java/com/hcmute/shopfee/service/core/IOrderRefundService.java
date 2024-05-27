package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.payload.request.CreateOrderReturnRequest;
import com.hcmute.shopfee.payload.response.GetOrderRefundResponse;
import com.hcmute.shopfee.enums.param.AnswerStatus;

public interface IOrderRefundService {
    void createOrderRefundRequest(CreateOrderReturnRequest body, String orderId);
    void processOrderRefundRequest(AnswerStatus status, String orderId);
    GetOrderRefundResponse getOrderRefundRequest(String orderId);
}
