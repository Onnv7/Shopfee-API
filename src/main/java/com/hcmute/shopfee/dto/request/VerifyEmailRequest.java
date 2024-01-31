package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class VerifyEmailRequest {
    @Schema(example = EMAIL_EX)
    @NotBlank
    private String email;
    @Schema(example = CODE_EX)
    @NotBlank
    private String code;
}
