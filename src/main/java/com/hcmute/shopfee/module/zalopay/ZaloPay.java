package com.hcmute.shopfee.module.zalopay;

import com.hcmute.shopfee.module.zalopay.order.OrderZaloAPI;
import com.hcmute.shopfee.module.zalopay.refund.RefundZaloAPI;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class ZaloPay {
    private final String CALLBACK_URL;
    private final String REDIRECT_URL;
    private final String APP_ID;
    private final String KEY1;
    private final String KEY2;
    private final OrderZaloAPI orderZaloAPI;
    private final RefundZaloAPI refundZaloAPI;
    public String getCallbackUrl() {
        return CALLBACK_URL;
    }
    public String getRedirectUrl() {
        return REDIRECT_URL;
    }

    public String getAppId() {
        return APP_ID;
    }

    public String getKey1() {
        return KEY1;
    }

    public String getKey2() {
        return KEY2;
    }

    public ZaloPay(String REDIRECT_URL, String CALLBACK_URL, String APP_ID, String KEY1, String KEY2) {
        this.REDIRECT_URL = REDIRECT_URL;
        this.CALLBACK_URL = CALLBACK_URL;
        this.APP_ID = APP_ID;
        this.KEY1 = KEY1;
        this.KEY2 = KEY2;
        orderZaloAPI = new OrderZaloAPI(this);
        refundZaloAPI = new RefundZaloAPI(this);
    }

    public Map<String, Object> createOrderZaloPay(String appUser, long amount) throws IOException {
        return orderZaloAPI.createOrder(appUser, amount);
    }

    public Map<String, Object> getOrder(String appTransId) throws IOException, URISyntaxException {
        return orderZaloAPI.getOrder(appTransId);
    }

    public Map<String, Object> sendRefund(String zpTransId, long amount, String description) throws IOException {
        return refundZaloAPI.sendRefund(zpTransId, amount, description);
    }

    public Map<String, Object> getStatusRefund(String refundId) throws IOException, URISyntaxException {
        return refundZaloAPI.getStatusRefund(refundId);
    }
}
