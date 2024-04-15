package com.hcmute.shopfee.module.zalopay.order.dto.request;

import lombok.Data;

@Data
public class CallBackDto {
    private String data;
    private String mac;
    private int type;
}
