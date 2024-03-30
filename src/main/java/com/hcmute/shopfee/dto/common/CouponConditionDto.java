package com.hcmute.shopfee.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hcmute.shopfee.dto.common.coupon.condition.CombinationConditionDto;
import com.hcmute.shopfee.dto.common.coupon.condition.MinPurchaseConditionDto;
import com.hcmute.shopfee.dto.common.coupon.condition.SubjectConditionDto;
import com.hcmute.shopfee.dto.common.coupon.condition.UsageConditionDto;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.CombinationConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.MinPurchaseConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.SubjectConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.condition.UsageConditionEntity;
import com.hcmute.shopfee.enums.ConditionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

import static com.hcmute.shopfee.constant.SwaggerConstant.COUPON_CONDITION_DESCRIPTION_EX;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponConditionDto {
    private ConditionType type;
    private List<CombinationConditionDto> combinationConditionList;

    private MinPurchaseConditionDto minPurchaseCondition;

    private List<SubjectConditionDto> subjectConditionList;

    private List<UsageConditionDto> usageConditionList;

}
