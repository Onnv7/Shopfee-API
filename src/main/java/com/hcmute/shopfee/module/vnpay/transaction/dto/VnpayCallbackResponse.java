package com.hcmute.shopfee.module.vnpay.transaction.dto;

import lombok.Data;

@Data
public class VnpayCallbackResponse {
    private String RspCode;
    private String Message;
}
