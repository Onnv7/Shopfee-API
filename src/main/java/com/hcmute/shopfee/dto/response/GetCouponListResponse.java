package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.coupon.CouponEntity;
import com.hcmute.shopfee.enums.CouponStatus;
import com.hcmute.shopfee.enums.CouponType;
import lombok.Data;

@Data
public class GetCouponListResponse {
    private String id;
    private String code;
    private CouponType couponType;
    private boolean isExpired;
    private CouponStatus status;

    public static GetCouponListResponse fromCouponEntity(CouponEntity entity) {
        GetCouponListResponse response = new GetCouponListResponse();
        response.setId(entity.getId());
        response.setCode(entity.getCode());
        response.setCouponType(entity.getCouponType());
        // TODO xem chỗ isExpired
        response.setStatus(entity.getStatus());
        return response;
    }
}
