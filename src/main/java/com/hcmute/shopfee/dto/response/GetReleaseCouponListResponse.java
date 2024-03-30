package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.dto.common.CouponConditionDto;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import com.hcmute.shopfee.enums.CouponType;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GetReleaseCouponListResponse {
    private String id;
    private String description;
    private Date startDate;
    private Date expirationDate;
    private CouponType type;
    private List<CouponConditionDto> conditionList;

    public static GetReleaseCouponListResponse fromCouponEntity(CouponEntity entity) {
        GetReleaseCouponListResponse response = new GetReleaseCouponListResponse();
        response.setId(entity.getId());
        response.setDescription(entity.getDescription());
        response.setStartDate(entity.getStartDate());
        response.setExpirationDate(entity.getExpirationDate());
        response.setType(entity.getCouponType());
        return response;
    }
}
