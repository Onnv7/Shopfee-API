package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.OBJECT_ID_EX;

@Data
public class FirebaseRegisterRequest {
    @Schema(example = OBJECT_ID_EX)
//    @NotBlank
    private String fcmTokenId;
}
