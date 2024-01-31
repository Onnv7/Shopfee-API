package com.hcmute.shopfee.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeLoginResponse {
    private final String employeeId;
    private final String accessToken;
    private final String refreshToken;
}
