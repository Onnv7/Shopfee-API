package com.hcmute.shopfee.dto.common.vnpay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VNPayPaymentUrl {
    @JsonProperty("vnp_url")
    private String vnpUrl;
    @JsonProperty("vnp_CreateDate")
    private String vnpCreateDate;
    @JsonProperty("vnp_TxnRef")
    private String vnpTxnRef;
}
