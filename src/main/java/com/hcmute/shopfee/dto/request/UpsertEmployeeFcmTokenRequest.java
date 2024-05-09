package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.FCM_TOKEN_EX;
import static com.hcmute.shopfee.constant.SwaggerConstant.OBJECT_ID_EX;

@Data
public class UpsertEmployeeFcmTokenRequest {
    public UpsertEmployeeFcmTokenRequest() {
    }

    public UpsertEmployeeFcmTokenRequest(String employeeId, String token) {
        this.employeeId = employeeId;
        this.token = token;
    }

    @Schema(example = OBJECT_ID_EX)
    private String employeeId;

    @Schema(example = FCM_TOKEN_EX)
    @NotBlank
    private String token;
}
