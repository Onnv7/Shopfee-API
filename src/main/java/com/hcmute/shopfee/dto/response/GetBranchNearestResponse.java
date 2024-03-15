package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.database.BranchEntity;
import com.hcmute.shopfee.utils.DateUtils;
import lombok.Data;

@Data
public class GetBranchNearestResponse {

    private String id;
    private String name;
    private String openTime;
    private String closeTime;
    private String fullAddress;


    public static GetBranchNearestResponse fromBranchEntity(BranchEntity entity) {
        GetBranchNearestResponse data = new GetBranchNearestResponse();
        data.setId(entity.getId());
        data.setName(entity.getName());
        data.setOpenTime(DateUtils.formatHHmm(entity.getOpenTime()));
        data.setCloseTime(DateUtils.formatHHmm(entity.getCloseTime()));
        data.setFullAddress(entity.getFullAddress());
        return data;
    }

}
