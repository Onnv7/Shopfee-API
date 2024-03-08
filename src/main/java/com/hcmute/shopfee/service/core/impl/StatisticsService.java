package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.response.GetRevenueByTimeResponse;
import com.hcmute.shopfee.dto.response.GetRevenueCurrentDateResponse;
import com.hcmute.shopfee.dto.response.GetStatisticsOfOrderQuantityResponse;
import com.hcmute.shopfee.dto.sql.GetRevenueQueryDto;
import com.hcmute.shopfee.dto.sql.GetStatisticOfOrderQuantityQueryDto;
import com.hcmute.shopfee.dto.sql.RevenueStatisticsQueryDto;
import com.hcmute.shopfee.enums.TimeUnit;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.TransactionRepository;
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
    public GetRevenueByTimeResponse getRevenueByTimeRange(Date startDate, Date endDate, TimeUnit timeUnit) {
        if(startDate.compareTo(endDate) > 0) {
            throw new CustomException(ErrorConstant.DATA_SEND_INVALID, "The start date must be less than the end date");
        }
        GetRevenueByTimeResponse data= new GetRevenueByTimeResponse();
        String formatTime = "%Y-%m-%d";
        switch(timeUnit) {
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

        List<RevenueStatisticsQueryDto> revenueStatistics = transactionRepository.getRevenueStatistics(startDate, endDate, formatTime);
        data.setRevenueList( GetRevenueByTimeResponse.Revenue.fromRevenueStatisticList(revenueStatistics));
        return data;
    }

    @Override
    public GetRevenueCurrentDateResponse getRevenueCurrentDate() {
        GetRevenueQueryDto revenueQueryDto = transactionRepository.getRevenueByDate(new Timestamp(System.currentTimeMillis()));
        return GetRevenueCurrentDateResponse.fromRevenueQueryDto(revenueQueryDto);
    }

    @Override
    public GetStatisticsOfOrderQuantityResponse getStatisticOfOrderQuantity() {
        GetStatisticOfOrderQuantityQueryDto queryDto = orderBillRepository.getStatisticOfOrderQuantity();
        return GetStatisticsOfOrderQuantityResponse.fromStatisticOrderQuantityQuery(queryDto);
    }
}
