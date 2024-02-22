package com.hcmute.shopfee.dto.request;

import com.hcmute.shopfee.dto.common.coupon.condition.CombinationConditionDto;
import com.hcmute.shopfee.dto.common.coupon.condition.MinPurchaseConditionDto;
import com.hcmute.shopfee.dto.common.coupon.condition.UsageConditionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Data
public class CreateShippingCouponRequest {
    @Schema(example = COUPON_CODE_EX)
    @NotBlank
    private String code;

    @Schema(example = COUPON_DESCRIPTION_EX)
    @NotBlank
    private String description;

    @Schema()
    private List<UsageConditionDto> usageConditionList;

    @Schema()
    private List<CombinationConditionDto> combinationConditionList;

    @Schema()
    @NotNull
    private MinPurchaseConditionDto minPurchaseCondition;

//    @Schema()
//    @NotNull
//    private EligibilityCustomerConditionDto applicableCustomerCondition;


    @Schema(example = DATE_ISO_EX)
    @NotNull
    private Date startDate;

    @Schema(example = DATE_ISO_EX)
    private Date expirationDate;


}
