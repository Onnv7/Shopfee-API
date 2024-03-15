package com.hcmute.shopfee.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hcmute.shopfee.entity.database.BranchEntity;
import com.hcmute.shopfee.module.goong.distancematrix.reponse.DistanceMatrixResponse;
import com.hcmute.shopfee.utils.DateUtils;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.hcmute.shopfee.dto.response.GetBranchViewListResponse.BranchCard.fromBranchEntity;

@Data
public class GetBranchViewListResponse {
    private List<BranchCard> branchList;
    private Integer totalPage;
    @Data
    public static class BranchCard {
        private String id;
        private String imageUrl;
        private String name;
        private String fullAddress;
        private Double longitude;
        private Double latitude;
        private String distance;
        @JsonIgnore
        private int distanceValue;
        private String openTime;
        private String closeTime;

        static BranchCard fromBranchEntity(BranchEntity entity) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            BranchCard data = new BranchCard();
            data.setId(entity.getId());
            data.setImageUrl(entity.getImageUrl());
            data.setName(entity.getName());
            data.setFullAddress(entity.getFullAddress());
            data.setLongitude(entity.getLongitude());
            data.setLatitude(entity.getLatitude());
            data.setOpenTime(DateUtils.formatHHmm(entity.getOpenTime()));
            data.setCloseTime(DateUtils.formatHHmm(entity.getCloseTime()));
            return data;
        }

    }
    public static List<BranchCard> fromBranchEntityListAndFilterDistance(List<BranchEntity> entityList, List<DistanceMatrixResponse.Row.Element.Distance> distanceList) {
        List<BranchCard> data = new ArrayList<>();
        int size = entityList.size();
        for(int i=0; i<size; i++) {
            BranchCard card = fromBranchEntity(entityList.get(i));
            if(distanceList.get(i).getValue() > 12000) {
                continue;
            }
            card.setDistance(distanceList.get(i).getText());
            card.setDistanceValue(distanceList.get(i).getValue());
            data.add(card);
        }
        data.sort(Comparator.comparingInt(BranchCard::getDistanceValue));
        return data;
    }
}
