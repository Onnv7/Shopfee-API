package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeLoginResponse {
    private String employeeId;
    private String branchId;
    private String accessToken;
    @JsonIgnore
    private String refreshToken;
}
