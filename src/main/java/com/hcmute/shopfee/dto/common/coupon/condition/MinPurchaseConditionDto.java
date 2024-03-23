package com.hcmute.shopfee.dto.common.coupon.condition;

import com.hcmute.shopfee.entity.sql.database.coupon.condition.MinPurchaseConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.INTEGER_VALUE_EX;


@Data
public class MinPurchaseConditionDto {
//    @Schema(example = MIN_PURCHASE_TYPE)
//    @NotNull
//    private MiniPurchaseType type;

    @Schema(example = INTEGER_VALUE_EX)
    private Long value;

    public static MinPurchaseConditionDto fromMinPurchaseConditionEntity(MinPurchaseConditionEntity entity) {
        MinPurchaseConditionDto data = new MinPurchaseConditionDto();
//        data.setType(entity.getType());
        data.setValue(entity.getValue());
        return data;
    }
}
