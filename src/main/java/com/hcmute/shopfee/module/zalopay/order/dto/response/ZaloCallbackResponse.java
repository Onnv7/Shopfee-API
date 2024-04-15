package com.hcmute.shopfee.module.zalopay.order.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ZaloCallbackResponse {
    @JsonProperty("return_code")
    private int returnCode;
    @JsonProperty("return_message")
    private String returnMessage;
}
