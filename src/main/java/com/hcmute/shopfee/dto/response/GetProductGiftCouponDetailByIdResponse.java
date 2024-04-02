package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmute.shopfee.dto.common.coupon.condition.CombinationConditionDto;
import com.hcmute.shopfee.dto.common.coupon.condition.MinPurchaseConditionDto;
import com.hcmute.shopfee.dto.common.coupon.condition.SubjectConditionDto;
import com.hcmute.shopfee.dto.common.coupon.condition.UsageConditionDto;
import com.hcmute.shopfee.dto.common.coupon.reward.ProductRewardDto;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponConditionEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import com.hcmute.shopfee.entity.sql.database.coupon.reward.ProductRewardEntity;
import com.hcmute.shopfee.enums.CouponStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetProductGiftCouponDetailByIdResponse {
    private String id;
    private String code;
    private String description;
    private List<UsageConditionDto> usageConditionList;
    private List<CombinationConditionDto> combinationConditionList;
    private MinPurchaseConditionDto minPurchaseCondition;
    private List<SubjectConditionDto> subjectConditionList;
    private CouponStatus status;
    private Date startDate;
    private Date expirationDate;
    private List<ProductRewardDto> productRewardList;

    public static GetProductGiftCouponDetailByIdResponse fromCouponEntity(CouponEntity entity) {
        GetProductGiftCouponDetailByIdResponse data = new GetProductGiftCouponDetailByIdResponse();
        data.setId(entity.getId());
        data.setCode(entity.getCode());
        data.setDescription(entity.getDescription());
        data.setStatus(entity.getStatus());
        data.setStartDate(entity.getStartDate());
        data.setExpirationDate(entity.getExpirationDate());
        List<ProductRewardDto> productRewardList;
        List<ProductRewardEntity> productRewardEntityList = entity.getCouponReward().getProductRewardList();
        if(productRewardEntityList != null) {
            productRewardList = new ArrayList<>();
            for (ProductRewardEntity productRewardEntity: productRewardEntityList) {
                productRewardList.add(ProductRewardDto.fromProductRewardEntity(productRewardEntity));
            }
            data.setProductRewardList(productRewardList);
        }

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
                case SUBJECT_TYPE -> {
                    data.setSubjectConditionList(SubjectConditionDto.fromSubjectConditionEntityList(condition.getSubjectConditionList()));
                }
            }
        }
        return data;
    }
}
