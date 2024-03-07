package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.database.BranchEntity;
import lombok.Data;

@Data
public class GetBranchViewByIdResponse {
    private Long id;
    private String name;
    private String operatingTime;
    private String fullAddress;
    private Double longitude;
    private Double latitude;
    private String phoneNumber;
    private String imageUrl;

    public static GetBranchViewByIdResponse fromBranchEntity(BranchEntity entity) {
        GetBranchViewByIdResponse data = new GetBranchViewByIdResponse();
        data.setId(entity.getId());
        data.setName(entity.getName());
        data.setOperatingTime(entity.getOperatingTime());
        data.setFullAddress(entity.getFullAddress());
        data.setLatitude(entity.getLatitude());
        data.setLongitude(entity.getLongitude());
        data.setPhoneNumber(entity.getPhoneNumber());
        data.setImageUrl(entity.getImageUrl());
        return data;
    }
}
