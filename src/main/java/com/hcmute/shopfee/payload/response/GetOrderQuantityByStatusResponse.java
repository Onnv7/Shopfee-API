package com.hcmute.shopfee.payload.response;

import lombok.Data;

@Data
public class GetOrderQuantityByStatusResponse {
    private int orderQuantity;
    private int difference;
}
