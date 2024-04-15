package com.hcmute.shopfee.module.vnpay.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VnpayCallbackData {
    @JsonProperty("vnp_Amount")
    private String amount;

    @JsonProperty("vnp_BankCode")
    private String bankCode;

    @JsonProperty("vnp_BankTranNo")
    private String bankTranNo;

    @JsonProperty("vnp_CardType")
    private String cardType;

    @JsonProperty("vnp_OrderInfo")
    private String orderInfo;

    @JsonProperty("vnp_PayDate")
    private String payDate;

    @JsonProperty("vnp_ResponseCode")
    private String responseCode;

    @JsonProperty("vnp_TmnCode")
    private String tmnCode;

    @JsonProperty("vnp_TransactionNo")
    private String transactionNo;

    @JsonProperty("vnp_TransactionStatus")
    private String transactionStatus;

    @JsonProperty("vnp_TxnRef")
    private String txnRef;

    @JsonProperty("vnp_SecureHash")
    private String secureHash;
}
