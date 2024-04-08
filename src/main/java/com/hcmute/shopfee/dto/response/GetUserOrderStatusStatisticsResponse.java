package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.dto.sql.GetStatisticByKeyValue;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetUserOrderStatusStatisticsResponse {
    private Long total;
    private List<Statistics> statistics;
    @Data
    private static class Statistics {
        private String key;
        private Long value;

        private static Statistics fromOrderStatusStatisticDatabase(GetStatisticByKeyValue database) {
            Statistics data = new Statistics();
            data.setKey(database.getKey());
            data.setValue(database.getValue());
            return data;
        }
    }
    public static GetUserOrderStatusStatisticsResponse fromOrderStatusStatistics(List<GetStatisticByKeyValue> dataList) {
        List<Statistics> statistics = new ArrayList<>();
        GetUserOrderStatusStatisticsResponse data = new GetUserOrderStatusStatisticsResponse();
        Long total = 0L;
        for(GetStatisticByKeyValue itemData : dataList) {
            statistics.add(Statistics.fromOrderStatusStatisticDatabase(itemData));
            total += itemData.getValue();
        }
        data.setStatistics(statistics);
        data.setTotal(total);
        return data;
    }
}
