package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.coupon.CouponEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GetReleaseCouponByIdResponse {
    private List<String> conditionList;
    private String description;
    private String code;
    private Date startDate;
    private Date expirationDate;

    public static GetReleaseCouponByIdResponse fromCouponEntity(CouponEntity entity) {
        GetReleaseCouponByIdResponse response = new GetReleaseCouponByIdResponse();
        // TODO: xem lại conditionList
//        response.setId(entity.getId());
        response.setCode(entity.getCode());
        response.setDescription(entity.getDescription());
        response.setStartDate(entity.getStartDate());
        response.setExpirationDate(entity.getExpirationDate());
        return response;
    }
}
