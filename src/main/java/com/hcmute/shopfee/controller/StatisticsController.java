package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.SecurityConstant;
import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.response.GetRevenueByTimeResponse;
import com.hcmute.shopfee.dto.response.GetRevenueCurrentDateResponse;
import com.hcmute.shopfee.dto.response.GetStatisticsOfOrderQuantityResponse;
import com.hcmute.shopfee.enums.param.TimeUnit;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.core.IStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize(SecurityConstant.ROLE_ADMIN_MANAGER)
    public ResponseEntity<ResponseAPI<GetRevenueCurrentDateResponse>> getRevenueCurrentDate(
            @Parameter(name = "branch_id", required = false, example = "171473213040354")
            @RequestParam(name = "branch_id", required = false) String branchId) {
        GetRevenueCurrentDateResponse revenue = statisticsService.getRevenueCurrentDate(branchId);
        ResponseAPI<GetRevenueCurrentDateResponse> res = ResponseAPI.<GetRevenueCurrentDateResponse>builder()
                .message(SuccessConstant.GET)
                .data(revenue)
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = STATISTICS_GET_ORDER_QUANTITY_BY_STAGE_SUM)
    @GetMapping(path = GET_STATISTICS_QUANTITY_BY_STAGE_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN_MANAGER)
    public ResponseEntity<ResponseAPI<GetStatisticsOfOrderQuantityResponse>> getStatisticOfOrderQuantity(
            @Parameter(name = "branch_id", required = false, example = "171473213040354")
            @RequestParam(name = "branch_id", required = false) String branchId) {
        GetStatisticsOfOrderQuantityResponse resData = statisticsService.getStatisticOfOrderQuantity(branchId);
        ResponseAPI<GetStatisticsOfOrderQuantityResponse> res = ResponseAPI.<GetStatisticsOfOrderQuantityResponse>builder()
                .message(SuccessConstant.GET)
                .data(resData)
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = STATISTICS_GET_REVENUE_BY_TIME_SUM)
    @GetMapping(path = GET_STATISTICS_REVENUE_BY_TIME_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN_MANAGER)
    public ResponseEntity<ResponseAPI<GetRevenueByTimeResponse>> getRevenueByTime(
            @Parameter(name = "start_date", required = true, example = "2024-02-24")
            @RequestParam("start_date") Date startDate,
            @Parameter(name = "end_date", required = true, example = "2024-03-25")
            @RequestParam("end_date") Date endDate,
            @Parameter(name = "time_type", required = true, example = "day")
            @RequestParam("time_type") TimeUnit timeUnit,
            @Parameter(name = "branch_id", required = false, example = "171473213040354")
            @RequestParam(name = "branch_id", required = false) String branchId

    ) {
        GetRevenueByTimeResponse newData = statisticsService.getRevenueByTimeRange(startDate, endDate, timeUnit, branchId);
        ResponseAPI<GetRevenueByTimeResponse> res = ResponseAPI.<GetRevenueByTimeResponse>builder()
                .message(SuccessConstant.GET)
                .data(newData)
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
