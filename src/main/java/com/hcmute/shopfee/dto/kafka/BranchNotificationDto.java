package com.hcmute.shopfee.dto.kafka;

import lombok.Data;

@Data
public class BranchNotificationDto {
    private String branchId;
    private String title;
    private String body;
}
