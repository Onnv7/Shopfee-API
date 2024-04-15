package com.hcmute.shopfee.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class BranchNotificationDto {
    private String branchId;
    private String title;
    private String body;

    public BranchNotificationDto() {
    }

    public BranchNotificationDto(String branchId, String title, String body) {
        this.branchId = branchId;
        this.title = title;
        this.body = body;
    }
}
