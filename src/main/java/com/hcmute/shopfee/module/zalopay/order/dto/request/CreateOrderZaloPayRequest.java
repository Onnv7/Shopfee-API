package com.hcmute.shopfee.module.zalopay.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderZaloPayRequest {
    private String appUser;
    private Long amount;
    private String orderId;
}

