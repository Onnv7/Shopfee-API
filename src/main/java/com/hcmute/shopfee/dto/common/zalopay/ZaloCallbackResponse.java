package com.hcmute.shopfee.dto.common.zalopay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ZaloCallbackResponse {
    @JsonProperty("return_code")
    private int returnCode;
    @JsonProperty("return_message")
    private String returnMessage;
}
