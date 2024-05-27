package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.payload.response.GetRevenueByTimeResponse;
import com.hcmute.shopfee.payload.response.GetRevenueCurrentTimeResponse;
import com.hcmute.shopfee.payload.response.GetStatisticsOfOrderQuantityResponse;
import com.hcmute.shopfee.enums.param.TimeUnit;

import java.sql.Date;

public interface IStatisticsService {
    GetRevenueByTimeResponse getRevenueByTimeRange(Date startDate, Date endDate, TimeUnit timeUnit, String branchId);
    GetRevenueCurrentTimeResponse getRevenueCurrent(String branchId);
    GetStatisticsOfOrderQuantityResponse getStatisticOfOrderQuantity(String branchId, TimeUnit timeUnit);
}
