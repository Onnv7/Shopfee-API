package com.hcmute.shopfee.module.zalopay.order.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetOrderZaloPayResponse {
    @JsonProperty("amount")
    private int amount;

    @JsonProperty("discount_amount")
    private int discountAmount;

    @JsonProperty("zp_trans_id")
    private long zpTransId;

    @JsonProperty("return_message")
    private String returnMessage;

    @JsonProperty("sub_return_message")
    private String subReturnMessage;

    @JsonProperty("sub_return_code")
    private int subReturnCode;

    @JsonProperty("is_processing")
    private boolean isProcessing;

    @JsonProperty("return_code")
    private int returnCode;

    @JsonProperty("server_time")
    private long serverTime;
}
