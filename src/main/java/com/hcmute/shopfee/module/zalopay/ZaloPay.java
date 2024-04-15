package com.hcmute.shopfee.module.zalopay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hcmute.shopfee.module.zalopay.order.OrderZaloAPI;
import com.hcmute.shopfee.module.zalopay.order.dto.request.CallBackDto;
import com.hcmute.shopfee.module.zalopay.order.dto.request.CallbackDataRequest;
import com.hcmute.shopfee.module.zalopay.order.dto.request.CreateOrderZaloPayRequest;
import com.hcmute.shopfee.module.zalopay.order.dto.request.GetOrderZaloPayRequest;
import com.hcmute.shopfee.module.zalopay.order.dto.response.CreateOrderZaloPayResponse;
import com.hcmute.shopfee.module.zalopay.order.dto.response.GetOrderZaloPayResponse;
import com.hcmute.shopfee.module.zalopay.refund.RefundZaloAPI;
import com.hcmute.shopfee.module.zalopay.refund.dto.request.RefundRequestDTO;
import com.hcmute.shopfee.module.zalopay.refund.dto.request.RefundStatusRequestDTO;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class ZaloPay {
    private final String APP_ID;
    private final String KEY1;
    private final String KEY2;
    private final OrderZaloAPI orderZaloAPI;
    private final RefundZaloAPI refundZaloAPI;

    public String getAppId() {
        return APP_ID;
    }

    public String getKey1() {
        return KEY1;
    }

    public String getKey2() {
        return KEY2;
    }

    public ZaloPay(String APP_ID, String KEY1, String KEY2) {
        this.APP_ID = APP_ID;
        this.KEY1 = KEY1;
        this.KEY2 = KEY2;
        orderZaloAPI = new OrderZaloAPI(this);
        refundZaloAPI = new RefundZaloAPI(this);
    }

    public CreateOrderZaloPayResponse createOrderZaloPay(CreateOrderZaloPayRequest createOrderZaloPayRequest) throws IOException {
        return orderZaloAPI.createOrder(createOrderZaloPayRequest);
    }

    public GetOrderZaloPayResponse getOrder(GetOrderZaloPayRequest body) throws IOException, URISyntaxException {
        return orderZaloAPI.getOrder(body);
    }

    public Map<String, Object> sendRefund(RefundRequestDTO refundRequestDTO) throws IOException {
        return refundZaloAPI.sendRefund(refundRequestDTO);
    }

    public Map<String, Object> getStatusRefund(String refundId) throws IOException, URISyntaxException {
        return refundZaloAPI.getStatusRefund(refundId);
    }

    public CallbackDataRequest getDataCallBack(String dataJson) throws JsonProcessingException {
        return orderZaloAPI.getDataCallBack(dataJson);
    }
}
