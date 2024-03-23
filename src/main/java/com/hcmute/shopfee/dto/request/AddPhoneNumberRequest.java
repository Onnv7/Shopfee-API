package com.hcmute.shopfee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.PHONE_NUMBER_EX;
import static com.hcmute.shopfee.constant.SwaggerConstant.PHONE_NUMBER_REGEX;

@Data
public class AddPhoneNumberRequest {

    @Schema(example = PHONE_NUMBER_EX)
    @NotBlank
    @Pattern(regexp = PHONE_NUMBER_REGEX)
    private String phoneNumber;
}
