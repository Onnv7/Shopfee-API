package com.hcmute.shopfee.payload.response;

import com.hcmute.shopfee.dto.sql.GetRevenueQueryDto;
import lombok.Data;

@Data
public class GetRevenueCurrentTimeResponse {
    private double revenueByToday;
    private double revenueByThisMonth;
    private double revenueByThisYear;

    public static GetRevenueCurrentTimeResponse fromRevenueQueryDto(GetRevenueQueryDto revenueQueryDto) {
        GetRevenueCurrentTimeResponse data = new GetRevenueCurrentTimeResponse();
        data.setRevenueByThisYear(revenueQueryDto.getRevenueByThisYear() != null ? revenueQueryDto.getRevenueByThisYear() : 0);
        data.setRevenueByToday(revenueQueryDto.getRevenueByToday() != null ? revenueQueryDto.getRevenueByToday() : 0);
        data.setRevenueByThisMonth(revenueQueryDto.getRevenueByThisMonth() != null ? revenueQueryDto.getRevenueByThisMonth() : 0);
        return data;
    }
}
