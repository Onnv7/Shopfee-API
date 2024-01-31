package com.hcmute.shopfee.dto.response;

import lombok.Data;

@Data
public class GetAddressListByUserIdResponse {
    private String id;
    private String detail;
    private String recipientName;
    private boolean isDefault;
    private String phoneNumber;
}
