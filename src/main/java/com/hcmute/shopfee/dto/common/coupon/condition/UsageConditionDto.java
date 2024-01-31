package com.hcmute.shopfee.dto.common.coupon.condition;

import com.hcmute.shopfee.dto.common.CouponConditionDto;
import com.hcmute.shopfee.entity.coupon.condition.UsageConditionEntity;
import com.hcmute.shopfee.enums.UsageConditionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.INTEGER_VALUE_EX;
import static com.hcmute.shopfee.constant.SwaggerConstant.USAGE_CONDITION_EX;

@Data
public class UsageConditionDto  {
    @Schema(example = USAGE_CONDITION_EX)
    @NotNull
    private UsageConditionType type;

    @Schema(example = INTEGER_VALUE_EX)
    @NotNull
    private Integer value;
}
