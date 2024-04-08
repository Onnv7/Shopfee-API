package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.dto.sql.GetUserSpendingStatisticDto;
import lombok.Data;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Data
public class GetUserSpendingStatisticsResponse {
    private Long totalSpent;
    private List<Statistics> statistics;

    @Data
    private static class Statistics {
        private String time;
        private Long amount;

        private static Statistics fromDatabase(GetUserSpendingStatisticDto database) {
            Statistics data = new Statistics();
            data.setTime(database.getTime().toString());
            data.setAmount(database.getAmount());
            return data;
        }
    }
    public static GetUserSpendingStatisticsResponse fromDatabase(List<GetUserSpendingStatisticDto> dataList, Date startDate, Date endDate) {
        GetUserSpendingStatisticsResponse data = new GetUserSpendingStatisticsResponse();
        List<Statistics> statistics = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        Long total = 0L;
        while (!calendar.getTime().after(endDate)) {
            Date currentDate = new Date(calendar.getTime().getTime());

            GetUserSpendingStatisticDto item = dataList.stream().filter(it -> it.getTimeDate().compareTo(currentDate) == 0).findFirst().orElse(null);
            if(item != null) {
                statistics.add(Statistics.fromDatabase(item));
                total += item.getAmount();
            } else {
                Statistics statisticsItem = new Statistics();
                statisticsItem.setTime(currentDate.toString());
                statisticsItem.setAmount(0L);
                statistics.add(statisticsItem);
            }
            calendar.add(Calendar.DATE, 1);
        }
        data.setStatistics(statistics);
        data.setTotalSpent(total);
        return data;
    }
}
