package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.database.coupon.CouponEntity;
import lombok.Data;

import java.util.Date;

@Data
public class GetReleaseCouponListResponse {
    private String id;
    private String description;
    private Date startDate;
    private Date expirationDate;

    public static GetReleaseCouponListResponse fromCouponEntity(CouponEntity entity) {
        GetReleaseCouponListResponse response = new GetReleaseCouponListResponse();
        response.setId(entity.getId());
        response.setDescription(entity.getDescription());
        response.setStartDate(entity.getStartDate());
        response.setExpirationDate(entity.getExpirationDate());
        return response;
    }
}
