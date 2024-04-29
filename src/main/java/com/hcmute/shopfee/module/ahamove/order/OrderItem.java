package com.hcmute.shopfee.module.ahamove.order;

import lombok.Data;

@Data
public class OrderItem {
    private String id;
    private String name;
    private Integer num;
    private Integer price;
}
