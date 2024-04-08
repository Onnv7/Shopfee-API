package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.dto.sql.GetEmployeeOrderStatisticDto;
import com.hcmute.shopfee.dto.sql.GetUserSpendingStatisticDto;
import lombok.Data;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Data
public class GetSaleStatisticTodayResponse {
    private Long totalItemPrice;
    private Integer totalOrderCount;
    private List<Statistics> statistics;

    @Data
    private static class Statistics {
        private String time;
        private Integer orderCount;
        private Long totalItemPrice;
        private static Statistics fromDatabase(GetEmployeeOrderStatisticDto record) {
            Statistics data = new Statistics();
            data.setTime(record.getTime());
            data.setOrderCount(record.getOrderCount());
            data.setTotalItemPrice(record.getTotalItemPrice());
            return data;
        }


    }
    public static GetSaleStatisticTodayResponse fromEmployeeOrderStatisticRecordList(List<GetEmployeeOrderStatisticDto> dataList, Date startDate, Date endDate) {
        GetSaleStatisticTodayResponse data = new GetSaleStatisticTodayResponse();
        List<Statistics> statistics = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        Long totalPrice = 0L;
        Integer totalCount = 0;
        while (!calendar.getTime().after(endDate)) {
            Date currentDate = new Date(calendar.getTime().getTime());

            GetEmployeeOrderStatisticDto item = dataList.stream().filter(it -> it.getTimeDate().compareTo(currentDate) == 0).findFirst().orElse(null);
            if(item != null) {
                statistics.add(Statistics.fromDatabase(item));
                totalPrice += item.getTotalItemPrice();
                totalCount += item.getOrderCount();
            } else {
                Statistics statisticsItem = new Statistics();
                statisticsItem.setTime(currentDate.toString());
                statisticsItem.setOrderCount(0);
                statisticsItem.setTotalItemPrice(0L);
                statistics.add(statisticsItem);
            }
            calendar.add(Calendar.DATE, 1);
        }
        data.setStatistics(statistics);
        data.setTotalItemPrice(totalPrice);
        data.setTotalOrderCount(totalCount);
        return data;
    }
}
