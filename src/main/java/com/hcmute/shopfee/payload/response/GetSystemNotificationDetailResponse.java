package com.hcmute.shopfee.payload.response;

import lombok.Data;

import java.util.Date;

@Data
public class GetSystemNotificationDetailResponse {
    private String id;
    private String title;
    private String content;
    private String imageUrl;
    private Date triggerTime;
}
