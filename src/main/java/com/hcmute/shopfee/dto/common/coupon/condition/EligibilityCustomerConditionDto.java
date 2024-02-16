package com.hcmute.shopfee.dto.common.coupon.condition;

import com.hcmute.shopfee.enums.ApplicableCustomerType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class EligibilityCustomerConditionDto {
    @Schema(example = APPLICABLE_CUSTOMER_TYPE)
    @NotNull
    private ApplicableCustomerType type;

    @Schema(example = OBJECT_ID_EX)
    @NotNull
    private String value;
}
