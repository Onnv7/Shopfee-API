package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.dto.sql.GetRevenueQueryDto;
import lombok.Data;

@Data
public class GetRevenueCurrentDateResponse {
    private double revenueByToday;
    private double revenueByThisMonth;
    private double revenue;

    public static GetRevenueCurrentDateResponse fromRevenueQueryDto(GetRevenueQueryDto revenueQueryDto) {
        GetRevenueCurrentDateResponse data = new GetRevenueCurrentDateResponse();
        data.setRevenue(revenueQueryDto.getRevenue());
        data.setRevenueByToday(revenueQueryDto.getRevenueByToday());
        data.setRevenueByThisMonth(revenueQueryDto.getRevenueByThisMonth());
        return data;
    }
}
