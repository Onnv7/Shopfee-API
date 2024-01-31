package com.hcmute.shopfee.dto.common.coupon.reward;

import com.hcmute.shopfee.enums.MoneyRewardUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.COUPON_UNIT_EX;
import static com.hcmute.shopfee.constant.SwaggerConstant.OBJECT_ID_EX;

@Data
public class ProductRewardDto {
    @Schema(example = OBJECT_ID_EX)
    @NotNull
    private String productId;

    @Schema(example = OBJECT_ID_EX)
    @NotNull
    private Short quantity;
}
