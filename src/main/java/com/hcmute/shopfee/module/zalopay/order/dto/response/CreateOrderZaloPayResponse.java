package com.hcmute.shopfee.module.zalopay.order.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateOrderZaloPayResponse {
    @JsonProperty("order_url")
    private String orderUrl;

    @JsonProperty("order_token")
    private String orderToken;

    @JsonProperty("return_message")
    private String returnMessage;

    @JsonProperty("sub_return_message")
    private String subReturnMessage;

    @JsonProperty("sub_return_code")
    private String subReturnCode;

    @JsonProperty("zp_trans_token")
    private String zpTransToken;

    @JsonProperty("return_code")
    private String returnCode;

    private String invoiceCode;
}
