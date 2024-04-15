package com.hcmute.shopfee.module.zalopay.order.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CallbackDataRequest {
    @JsonProperty("app_id")
    private int appId;

    @JsonProperty("app_trans_id")
    private String appTransId;

    @JsonProperty("app_time")
    private long appTime;

    @JsonProperty("app_user")
    private String appUser;

    private int amount;

    @JsonProperty("embed_data")
    private String embedData;

    private String item;

    @JsonProperty("zp_trans_id")
    private long zpTransId;

    @JsonProperty("server_time")
    private long serverTime;

    private int channel;

    @JsonProperty("merchant_user_id")
    private String merchantUserId;

    @JsonProperty("zp_user_id")
    private String zpUserId;

    @JsonProperty("user_fee_amount")
    private int userFeeAmount;

    @JsonProperty("discount_amount")
    private int discountAmount;
}
