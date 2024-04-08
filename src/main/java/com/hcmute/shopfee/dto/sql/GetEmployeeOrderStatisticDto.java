package com.hcmute.shopfee.dto.sql;

import com.hcmute.shopfee.utils.DateUtils;

import java.sql.Date;

public interface GetEmployeeOrderStatisticDto {
    String getTime();
    default Date getTimeDate() {
        return DateUtils.getSqlDateFromTimeString(getTime());
    }
    Long getTotalItemPrice();
    Integer getOrderCount();
}
