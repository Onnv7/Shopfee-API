package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.dto.common.CouponConditionDto;
import com.hcmute.shopfee.entity.sql.database.coupon.CouponEntity;
import com.hcmute.shopfee.enums.CouponType;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class GetReleaseCouponListResponse {
    private String id;
    private String description;
    private Date startDate;
    private Date expirationDate;
    private CouponType type;

    private static GetReleaseCouponListResponse fromCouponEntity(CouponEntity entity) {
        GetReleaseCouponListResponse data = new GetReleaseCouponListResponse();
        data.setId(entity.getId());
        data.setDescription(entity.getDescription());
        data.setStartDate(entity.getStartDate());
        data.setExpirationDate(entity.getExpirationDate());
        data.setType(entity.getCouponType());
        return data;
    }

    public static List<GetReleaseCouponListResponse> fromCouponEntityList(List<CouponEntity> entityList) {
        List<GetReleaseCouponListResponse> data = new ArrayList<>();
        for (CouponEntity entity : entityList) {
            data.add(fromCouponEntity(entity));
        }
        return data;
    }
}
