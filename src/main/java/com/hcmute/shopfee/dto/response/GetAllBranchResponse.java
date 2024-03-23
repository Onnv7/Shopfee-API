package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.sql.database.BranchEntity;
import com.hcmute.shopfee.enums.BranchStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetAllBranchResponse {
    private List<BranchInfo> branchList;
    private Integer totalPage;

    @Data
    public static class BranchInfo {
        private String id;
        private String name;
        private BranchStatus status;
        private String phoneNumber;
        private String fullAddress;
        private String operatingTime;

        private static BranchInfo fromBranchEntity(BranchEntity entity) {
            BranchInfo data = new BranchInfo();
            data.setId(entity.getId());
            data.setName(entity.getName());
            data.setStatus(entity.getStatus());
            data.setPhoneNumber(entity.getPhoneNumber());
            data.setFullAddress(entity.getFullAddress());
            data.setOperatingTime(entity.getOperatingTime());
            return data;
        }
        public static List<BranchInfo> fromBranchEntityList(List<BranchEntity> entityList) {
            List<BranchInfo> data = new ArrayList<>();
            for (BranchEntity entity : entityList) {
                data.add(fromBranchEntity(entity));
            }
            return data;
        }
    }


}
