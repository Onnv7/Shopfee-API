package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.sql.database.BranchEntity;
import com.hcmute.shopfee.utils.DateUtils;
import lombok.Data;

@Data
public class GetBranchViewByIdResponse {
    private String id;
    private String name;
    private String openTime;
    private String closeTime;
    private String fullAddress;
    private Double longitude;
    private Double latitude;
    private String phoneNumber;
    private String imageUrl;

    public static GetBranchViewByIdResponse fromBranchEntity(BranchEntity entity) {
        GetBranchViewByIdResponse data = new GetBranchViewByIdResponse();
        data.setId(entity.getId());
        data.setName(entity.getName());
        data.setOpenTime(DateUtils.formatHHmm(entity.getOpenTime()));
        data.setCloseTime(DateUtils.formatHHmm(entity.getCloseTime()));
        data.setFullAddress(entity.getFullAddress());
        data.setLatitude(entity.getLatitude());
        data.setLongitude(entity.getLongitude());
        data.setPhoneNumber(entity.getPhoneNumber());
        data.setImageUrl(entity.getImageUrl());
        return data;
    }
}
