package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.EMAIL_EX;

@Data
public class SendCodeRequest {
    @Schema(example = EMAIL_EX)
    @Email
    @NotBlank
    private String email;

}
