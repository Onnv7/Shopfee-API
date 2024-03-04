package com.hcmute.shopfee.dto.common.coupon.condition;

import com.hcmute.shopfee.entity.database.coupon.condition.SubjectConditionEntity;
import com.hcmute.shopfee.enums.TargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class SubjectConditionDto {
//    @Schema(example = TARGET_OBJECT_CONDITION_EX)
//    @NotNull
//    private TargetType type;

    @Schema(example = OBJECT_ID_EX)
    @NotNull
    private String objectId;

    @Schema(example = COUPON_QUANTITY_EX)
    @NotNull
    private Integer value;

    public static SubjectConditionDto fromSubjectConditionEntity(SubjectConditionEntity entity) {
        SubjectConditionDto data = new SubjectConditionDto();
//        data.setType(entity.getType());
        data.setValue(entity.getValue());
        data.setObjectId(entity.getObjectId());
        return data;
    }

    public static List<SubjectConditionDto> fromSubjectConditionEntityList(List<SubjectConditionEntity> entityList) {
        List<SubjectConditionDto> data = new ArrayList<>();
        for (SubjectConditionEntity entity : entityList) {
            data.add(fromSubjectConditionEntity(entity));
        }
        return data;
    }
}
