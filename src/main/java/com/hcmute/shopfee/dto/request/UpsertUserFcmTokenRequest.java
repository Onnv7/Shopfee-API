package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class UpsertUserFcmTokenRequest {
    public UpsertUserFcmTokenRequest() {
    }

    public UpsertUserFcmTokenRequest(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    @Schema(example = OBJECT_ID_EX)
    private String userId;

    @Schema(example = FCM_TOKEN_EX)
    @NotBlank
    private String token;
}
