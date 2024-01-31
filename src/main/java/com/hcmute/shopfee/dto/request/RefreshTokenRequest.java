package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.REFRESH_TOKEN_EX;

@Data
public class RefreshTokenRequest {

    @Schema(example = REFRESH_TOKEN_EX)
    @NotBlank
    private String refreshToken;
}
