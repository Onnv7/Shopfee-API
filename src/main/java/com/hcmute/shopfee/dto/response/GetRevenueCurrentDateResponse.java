package com.hcmute.shopfee.dto.response;

import com.hcmute.shopfee.dto.sql.GetRevenueQueryDto;
import lombok.Data;

@Data
public class GetRevenueCurrentDateResponse {
    private double revenueByToday;
    private double revenueByThisMonth;
    private double revenueByThisYear;

    public static GetRevenueCurrentDateResponse fromRevenueQueryDto(GetRevenueQueryDto revenueQueryDto) {
        GetRevenueCurrentDateResponse data = new GetRevenueCurrentDateResponse();
        data.setRevenueByThisYear(revenueQueryDto.getRevenueByThisYear() != null ? revenueQueryDto.getRevenueByThisYear() : 0);
        data.setRevenueByToday(revenueQueryDto.getRevenueByToday() != null ? revenueQueryDto.getRevenueByToday() : 0);
        data.setRevenueByThisMonth(revenueQueryDto.getRevenueByThisMonth() != null ? revenueQueryDto.getRevenueByThisMonth() : 0);
        return data;
    }
}
