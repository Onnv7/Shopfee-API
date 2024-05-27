package com.hcmute.shopfee.payload.response;

import com.hcmute.shopfee.dto.common.CouponConditionDto;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import com.hcmute.shopfee.enums.CouponType;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GetReleaseCouponByIdResponse {
    private List<CouponConditionDto> conditionList;
    private String description;
    private String code;
    private Date startDate;
    private Date expirationDate;
    private CouponType type;

    public static GetReleaseCouponByIdResponse fromCouponEntity(CouponEntity entity, List<CouponConditionDto> conditionList) {
        GetReleaseCouponByIdResponse response = new GetReleaseCouponByIdResponse();
        response.setConditionList(conditionList);
        response.setCode(entity.getCode());
        response.setDescription(entity.getDescription());
        response.setStartDate(entity.getStartDate());
        response.setExpirationDate(entity.getExpirationDate());
        response.setType(entity.getCouponType());
        return response;
    }
}
