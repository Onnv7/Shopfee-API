package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.enums.BranchStatus;
import lombok.Data;

import java.sql.Time;

@Data
public class GetBranchDetailByIdResponse {
    private Long id;
    private String name;
    private String phoneNumber;
    private String imageUrl;
    private Double longitude;
    private Double latitude;
    private String province;
    private String district;
    private String ward;
    private String detail;
    private String openTime;
    private String closeTime;
    private BranchStatus status;
}
