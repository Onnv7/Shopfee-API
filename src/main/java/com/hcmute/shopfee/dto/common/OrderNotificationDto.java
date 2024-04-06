package com.hcmute.shopfee.dto.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderNotificationDto {
    private String clientId;
    private String title;
    private String body;
}
