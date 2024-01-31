package com.hcmute.shopfee.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.COUPON_CONDITION_DESCRIPTION_EX;

@Data
public class CouponConditionDto {
    @Schema(example = COUPON_CONDITION_DESCRIPTION_EX)
    @NotBlank
    private String description;
}
