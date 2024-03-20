package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeLoginResponse {
    private String employeeId;
    private String branchId;
    private String accessToken;
    @JsonIgnore
    private String refreshToken;
}
