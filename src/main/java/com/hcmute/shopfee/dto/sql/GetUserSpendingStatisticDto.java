package com.hcmute.shopfee.dto.sql;

import com.hcmute.shopfee.utils.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public interface GetUserSpendingStatisticDto {
    String getTime(); // Thêm phương thức để lấy chuỗi thời gian

    default Date getTimeDate() {
        return DateUtils.getSqlDateFromTimeString(getTime());
    }
    Long getAmount();
}
