package com.hcmute.shopfee.dto.response;

import lombok.Data;

@Data
public class GetRevenueByTimeResponse {
    private Time time;
    private double revenue;

    @Data
    private class Time {
        int year;
        int month;
        int day;
    }
}
