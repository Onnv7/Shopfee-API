package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeLoginResponse {
    private final String employeeId;
    private final String accessToken;
    @JsonIgnore
    private final String refreshToken;
}
