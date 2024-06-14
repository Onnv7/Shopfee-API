package com.hcmute.shopfee.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmute.shopfee.dto.common.coupon.condition.CombinationConditionDto;
import com.hcmute.shopfee.dto.common.coupon.condition.MinPurchaseConditionDto;
import com.hcmute.shopfee.dto.common.coupon.condition.UsageConditionDto;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.reward.MoneyRewardEntity;
import com.hcmute.shopfee.enums.CouponStatus;
import com.hcmute.shopfee.enums.MoneyRewardUnit;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetOrderCouponDetailByIdResponse {
    private String id;
    private String code;
    private String description;
    private List<UsageConditionDto> usageConditionList;
    private List<CombinationConditionDto> combinationConditionList;
    private MinPurchaseConditionDto minPurchaseCondition;
    private CouponStatus status;
    private Date startDate;
    private Date expirationDate;
    private MoneyRewardUnit unitReward;
    private Integer valueReward;

    public static GetOrderCouponDetailByIdResponse fromCouponEntity(CouponEntity entity) {
        GetOrderCouponDetailByIdResponse data = new GetOrderCouponDetailByIdResponse();
        data.setId(entity.getId());
        data.setCode(entity.getCode());
        data.setDescription(entity.getDescription());
        data.setStatus(entity.getStatus());
        data.setStartDate(entity.getStartDate());
        data.setExpirationDate(entity.getExpirationDate());
        data.setUnitReward(((MoneyRewardEntity) entity).getUnit());
        data.setValueReward(((MoneyRewardEntity) entity).getValue());

        List<CouponConditionEntity>  conditionEntityList = entity.getConditionList();
        for (CouponConditionEntity condition: conditionEntityList) {
            switch (condition.getType()) {
                case USAGE -> {
                    data.setUsageConditionList(UsageConditionDto.fromUsageConditionEntityList(condition.getUsageConditionList()));
                }
                case MIN_PURCHASE -> {
                    data.setMinPurchaseCondition(MinPurchaseConditionDto.fromMinPurchaseConditionEntity(condition.getMinPurchaseCondition()));
                }
                case COMBINATION -> {
                    data.setCombinationConditionList(CombinationConditionDto.fromCombinationConditionEntityList(condition.getCombinationConditionList()));
                }
            }
        }
        return data;
    }
}
