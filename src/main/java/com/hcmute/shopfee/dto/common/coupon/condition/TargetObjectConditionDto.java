package com.hcmute.shopfee.dto.common.coupon.condition;

import com.hcmute.shopfee.dto.common.CouponConditionDto;
import com.hcmute.shopfee.enums.TargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class TargetObjectConditionDto  {
    @Schema(example = TARGET_OBJECT_CONDITION_EX)
    @NotNull
    private TargetType type;

    @Schema(example = OBJECT_ID_EX)
    @NotNull
    private String value;
}
