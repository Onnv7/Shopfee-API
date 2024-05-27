package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.payload.response.GetRevenueByTimeResponse;
import com.hcmute.shopfee.payload.response.GetRevenueCurrentTimeResponse;
import com.hcmute.shopfee.payload.response.GetStatisticsOfOrderQuantityResponse;
import com.hcmute.shopfee.dto.sql.GetRevenueQueryDto;
import com.hcmute.shopfee.dto.sql.GetStatisticOfOrderQuantityQueryDto;
import com.hcmute.shopfee.dto.sql.RevenueStatisticsQueryDto;
import com.hcmute.shopfee.enums.param.TimeUnit;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.repository.database.payment.TransactionRepository;
import com.hcmute.shopfee.repository.database.order.OrderBillRepository;
import com.hcmute.shopfee.service.core.IStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService implements IStatisticsService {
    private final TransactionRepository transactionRepository;
    private final OrderBillRepository orderBillRepository;

    @Override
    public GetRevenueByTimeResponse getRevenueByTimeRange(Date startDate, Date endDate, TimeUnit timeUnit, String branchId) {
        if (startDate.compareTo(endDate) > 0) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID, "The start date must be less than the end date");
        }
        if (branchId == null) {
            branchId = "";
        }
        GetRevenueByTimeResponse data = new GetRevenueByTimeResponse();
        String formatTime = "%Y-%m-%d";
        switch (timeUnit) {
            case day -> {
                formatTime = "%Y-%m-%d";
            }
            case month -> {
                formatTime = "%Y-%m";
            }
            case year -> {
                formatTime = "%Y";
            }
        }

        List<RevenueStatisticsQueryDto> revenueStatistics = transactionRepository.getRevenueStatistics(startDate, endDate, formatTime, branchId);
        data.setRevenueList(GetRevenueByTimeResponse.Revenue.fromRevenueStatisticList(revenueStatistics));
        return data;
    }

    @Override
    public GetRevenueCurrentTimeResponse getRevenueCurrent(String branchId) {
        GetRevenueQueryDto revenueQueryDto = transactionRepository.getRevenueByDate(new Timestamp(System.currentTimeMillis()), branchId);
        return GetRevenueCurrentTimeResponse.fromRevenueQueryDto(revenueQueryDto);
    }

    @Override
    public GetStatisticsOfOrderQuantityResponse getStatisticOfOrderQuantity(String branchId, TimeUnit timeUnit) {
        String formatTime = "%Y-%m-%d";
        switch (timeUnit) {
            case day -> {
                formatTime = "%Y-%m-%d";
            }
            case month -> {
                formatTime = "%Y-%m";
            }
            case year -> {
                formatTime = "%Y";
            }
        }
        GetStatisticOfOrderQuantityQueryDto queryDto = orderBillRepository.getStatisticOfOrderQuantity(branchId, formatTime);
        return GetStatisticsOfOrderQuantityResponse.fromStatisticOrderQuantityQuery(queryDto);
    }
}
