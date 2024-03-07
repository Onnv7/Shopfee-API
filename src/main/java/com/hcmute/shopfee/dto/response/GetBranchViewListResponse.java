package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.entity.database.BranchEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.hcmute.shopfee.dto.response.GetBranchViewListResponse.BranchCard.fromBranchEntity;

@Data
public class GetBranchViewListResponse {
    private List<BranchCard> branchList;
    private Integer totalPage;
    @Data
    public static class BranchCard {
        private Long id;
        private String imageUrl;
        private String name;
        private String fullAddress;
        private Double longitude;
        private Double latitude;
        static BranchCard fromBranchEntity(BranchEntity entity) {
            BranchCard data = new BranchCard();
            data.setId(entity.getId());
            data.setImageUrl(entity.getImageUrl());
            data.setName(entity.getName());
            data.setFullAddress(entity.getFullAddress());
            data.setLongitude(entity.getLongitude());
            data.setLatitude(entity.getLatitude());
            return data;
        }

    }
    public static List<BranchCard> fromBranchEntityList(List<BranchEntity> entityList) {
        List<BranchCard> data = new ArrayList<>();
        entityList.forEach(it -> {
            data.add(fromBranchEntity(it));
        });
        return data;
    }
}
