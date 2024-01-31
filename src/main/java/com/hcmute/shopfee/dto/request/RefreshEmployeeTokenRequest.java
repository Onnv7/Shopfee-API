package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.REFRESH_TOKEN_EX;

@Data
public class RefreshEmployeeTokenRequest {
    @Schema(example = REFRESH_TOKEN_EX)
    private String refreshToken;
}
