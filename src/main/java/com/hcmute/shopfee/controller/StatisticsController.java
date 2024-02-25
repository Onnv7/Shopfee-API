package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.response.GetRevenueByTimeResponse;
import com.hcmute.shopfee.dto.response.GetRevenueCurrentDateResponse;
import com.hcmute.shopfee.dto.response.GetStatisticsOfOrderQuantityResponse;
import com.hcmute.shopfee.enums.TimeUnit;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.core.IStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;

import static com.hcmute.shopfee.constant.RouterConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Tag(name = STATISTICS_CONTROLLER_TITLE)
@RestController
@RequestMapping(STATISTICS_BASE_PATH)
@RequiredArgsConstructor
public class StatisticsController {
    private final IStatisticsService statisticsService;

    @Operation(summary = STATISTICS_GET_REVENUE_CURRENT_DATE_SUM)
    @GetMapping(path = GET_STATISTICS_REVENUE_CURRENT_DATE_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetRevenueCurrentDateResponse>> getRevenueCurrentDate() {
        GetRevenueCurrentDateResponse revenue = statisticsService.getRevenueCurrentDate();
        ResponseAPI<GetRevenueCurrentDateResponse> res = ResponseAPI.<GetRevenueCurrentDateResponse>builder()
                .message(SuccessConstant.GET)
                .data(revenue)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = STATISTICS_GET_ORDER_QUANTITY_BY_STAGE_SUM)
    @GetMapping(path = GET_STATISTICS_QUANTITY_BY_STAGE_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetStatisticsOfOrderQuantityResponse>> getStatisticOfOrderQuantity() {
        GetStatisticsOfOrderQuantityResponse resData = statisticsService.getStatisticOfOrderQuantity();
        ResponseAPI<GetStatisticsOfOrderQuantityResponse> res = ResponseAPI.<GetStatisticsOfOrderQuantityResponse>builder()
                .message(SuccessConstant.GET)
                .data(resData)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = STATISTICS_GET_REVENUE_BY_TIME_SUM)
    @GetMapping(path = GET_STATISTICS_REVENUE_BY_TIME_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetRevenueByTimeResponse>> getRevenueByTime(
            @Parameter(name = "start_date", required = true, example = "2024-02-24")
            @RequestParam("start_date")  Date startDate,
            @Parameter(name = "end_date", required = true, example = "2024-03-25")
            @RequestParam("end_date") Date endDate,
            @RequestParam("time_type") TimeUnit timeUnit
    ) {
        GetRevenueByTimeResponse newData = statisticsService.getRevenueByTimeRange(startDate, endDate, timeUnit);
        ResponseAPI<GetRevenueByTimeResponse> res = ResponseAPI.<GetRevenueByTimeResponse>builder()
                .message(SuccessConstant.GET)
                .data(newData)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }
}
