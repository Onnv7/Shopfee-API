package com.hcmute.shopfee.dto.common;

import lombok.Data;

@Data
public class OrderNotificationDto {
    private String clientId;
    private String title;
    private String body;
}
