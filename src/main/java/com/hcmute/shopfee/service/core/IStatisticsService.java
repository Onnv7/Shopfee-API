package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.response.GetRevenueByTimeResponse;
import com.hcmute.shopfee.dto.response.GetRevenueCurrentDateResponse;
import com.hcmute.shopfee.dto.response.GetStatisticsOfOrderQuantityResponse;
import com.hcmute.shopfee.enums.TimeUnit;

import java.sql.Date;

public interface IStatisticsService {
    GetRevenueByTimeResponse getRevenueByTimeRange(Date startDate, Date endDate, TimeUnit timeUnit);
    GetRevenueCurrentDateResponse getRevenueCurrentDate();
    GetStatisticsOfOrderQuantityResponse getStatisticOfOrderQuantity();
}
