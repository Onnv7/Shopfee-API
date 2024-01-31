package com.hcmute.shopfee.dto.common.coupon.reward;

import com.hcmute.shopfee.enums.MoneyRewardUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class MoneyRewardDto {
    @Schema(example = COUPON_UNIT_EX)
    @NotNull
    private MoneyRewardUnit unit;

    @Schema(example = DISCOUNT_VALUE_EX)
    private Integer value;
}
