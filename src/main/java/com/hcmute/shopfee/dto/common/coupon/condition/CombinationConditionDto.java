package com.hcmute.shopfee.dto.common.coupon.condition;

import com.hcmute.shopfee.entity.database.coupon.condition.CombinationConditionEntity;
import com.hcmute.shopfee.enums.CombinationType;
import com.hcmute.shopfee.enums.CouponType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.hcmute.shopfee.constant.SwaggerConstant.COMBINATION_CONDITION_EX;

@Data
public class CombinationConditionDto {
    @Schema(example = COMBINATION_CONDITION_EX)
    @NotNull
    private CouponType type;

    public static CombinationConditionDto fromCombinationConditionEntity(CombinationConditionEntity entity) {
        CombinationConditionDto data= new CombinationConditionDto();
        data.setType(entity.getType());
        return data;
    }
    public static List<CombinationConditionDto> fromCombinationConditionEntityList(List<CombinationConditionEntity> entityList) {
        List<CombinationConditionDto> data = new ArrayList<>();
        for (CombinationConditionEntity entity : entityList) {
            data.add(fromCombinationConditionEntity(entity));
        }
        return data;
    }
}
