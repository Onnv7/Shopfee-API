package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.dto.sql.RevenueStatisticsQueryDto;
import lombok.Data;
import org.apache.commons.collections4.Get;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class GetRevenueByTimeResponse {
    List<Revenue> revenueList;

    @Data
    public static class Revenue {
        private String timePoint;
        private Long revenue;

        public static Revenue fromRevenueStatistics(RevenueStatisticsQueryDto revenueStatisticsQueryDto) {
            Revenue data = new Revenue();
            data.setTimePoint(revenueStatisticsQueryDto.getTime());
            data.setRevenue(revenueStatisticsQueryDto.getRevenue());
            return data;
        }
        public static List<Revenue> fromRevenueStatisticList(List<RevenueStatisticsQueryDto> revenueStatisticsQueryDtoList) {
            List<Revenue> data= new ArrayList<>();
            for (RevenueStatisticsQueryDto revenueStatisticsQueryDto : revenueStatisticsQueryDtoList) {
                data.add(fromRevenueStatistics(revenueStatisticsQueryDto));
            }
            return data;
        }
    }

}
