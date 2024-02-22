package com.hcmute.shopfee.dto.common.coupon.reward;

import com.hcmute.shopfee.enums.MoneyRewardUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class ProductRewardDto {
    @Schema(example = OBJECT_ID_EX)
    @NotNull
    private String productId;

    @Schema(example = PRODUCT_SIZE_EX)
    private String productSize;

    @Schema(example = OBJECT_ID_EX)
    @NotNull
    private Short quantity;
}
