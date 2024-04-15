package com.hcmute.shopfee.dto.common.vnpay;

import lombok.Data;

@Data
public class VnpayCallbackResponse {
    private String RspCode;
    private String Message;
}
