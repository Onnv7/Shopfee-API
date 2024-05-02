package com.hcmute.shopfee.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationDto {
    private String clientId;
    private String title;
    private String body;
}
