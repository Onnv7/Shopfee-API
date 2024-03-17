package com.hcmute.shopfee.module.vnpay.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PreTransactionInfo {
    @JsonProperty("vnp_url")
    private String vnpUrl;
    @JsonProperty("vnp_CreateDate")
    private String vnpCreateDate;
    @JsonProperty("vnp_TxnRef")
    private String vnpTxnRef;
}
