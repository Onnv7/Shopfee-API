package com.hcmute.shopfee.dto.response;

import lombok.Data;

@Data
public class GetAddressDetailsByIdResponse {
    private String id;
    private String detail;
    private double longitude;
    private double latitude;
    private String note;
    private String recipientName;
    private boolean isDefault;
    private String phoneNumber;
}
