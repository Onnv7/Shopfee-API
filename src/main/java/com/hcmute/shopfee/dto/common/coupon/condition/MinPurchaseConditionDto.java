package com.hcmute.shopfee.dto.common.coupon.condition;

import com.hcmute.shopfee.entity.database.coupon.condition.MinPurchaseConditionEntity;
import com.hcmute.shopfee.enums.MiniPurchaseType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.INTEGER_VALUE_EX;
import static com.hcmute.shopfee.constant.SwaggerConstant.MIN_PURCHASE_TYPE;


@Data
public class MinPurchaseConditionDto {
    @Schema(example = MIN_PURCHASE_TYPE)
    @NotNull
    private MiniPurchaseType type;

    @Schema(example = INTEGER_VALUE_EX)
    private Integer value;

    public static MinPurchaseConditionDto fromMinPurchaseConditionEntity(MinPurchaseConditionEntity entity) {
        MinPurchaseConditionDto data = new MinPurchaseConditionDto();
        data.setType(entity.getType());
        data.setValue(entity.getValue());
        return data;
    }
}
