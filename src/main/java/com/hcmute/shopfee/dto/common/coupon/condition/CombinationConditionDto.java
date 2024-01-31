package com.hcmute.shopfee.dto.common.coupon.condition;

import com.hcmute.shopfee.dto.common.CouponConditionDto;
import com.hcmute.shopfee.entity.coupon.condition.CombinationConditionEntity;
import com.hcmute.shopfee.enums.CombinationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.APPLICABLE_CUSTOMER_TYPE;
import static com.hcmute.shopfee.constant.SwaggerConstant.COMBINATION_CONDITION_EX;

@Data
public class CombinationConditionDto {
    @Schema(example = COMBINATION_CONDITION_EX)
    @NotNull
    private CombinationType type;
}
