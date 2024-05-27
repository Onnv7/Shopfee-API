package com.hcmute.shopfee.payload.response;

import com.hcmute.shopfee.entity.sql.database.CoinHistoryEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class GetCoinHistoryListResponse {
    private int totalPage;
    private List<CoinHistory> coinHistoryList;

    @Data
    private static class CoinHistory {
        private String description;
        private long coin;
        private Date createdAt;

        private static CoinHistory fromCoinHistoryEntity(CoinHistoryEntity entity) {
            CoinHistory data = new CoinHistory();
            data.setCoin(entity.getCoin());
            data.setDescription(entity.getDescription());
            data.setCreatedAt(entity.getCreatedAt());
            return data;
        }
    }

    public static List<CoinHistory> fromCoinHistoryEntityList(List<CoinHistoryEntity> entityList) {
        List<CoinHistory> data = new ArrayList<>();

        for(CoinHistoryEntity entity : entityList) {
            data.add(CoinHistory.fromCoinHistoryEntity(entity));
        }
        return data;
    }
}
