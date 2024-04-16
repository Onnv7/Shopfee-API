package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import com.hcmute.shopfee.enums.CouponRewardType;
import com.hcmute.shopfee.enums.CouponStatus;
import com.hcmute.shopfee.enums.CouponType;
import lombok.Data;

import java.util.Date;

@Data
public class GetCouponListResponse {
    private String id;
    private String code;
    private CouponType couponType;
    private Boolean isExpired;
    private CouponStatus status;
    private CouponRewardType rewardType;


    public static GetCouponListResponse fromCouponEntity(CouponEntity entity) {
        GetCouponListResponse data = new GetCouponListResponse();
        Date date = new Date();
        data.setId(entity.getId());
        data.setCode(entity.getCode());
        data.setCouponType(entity.getCouponType());
        data.setRewardType(entity.getRewardType());
        // TODO xem chỗ isExpired
        if(entity.getExpirationDate() == null) {
            data.setIsExpired(null);
        }
        else {
            data.setIsExpired(date.after(entity.getExpirationDate()));
        }

        data.setStatus(entity.getStatus());
        return data;
    }
}
