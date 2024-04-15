package com.hcmute.shopfee.dto.common.zalopay;

import lombok.Data;

@Data
public class CallBackDto {
    private String data;
    private String mac;
    private int type;
}
