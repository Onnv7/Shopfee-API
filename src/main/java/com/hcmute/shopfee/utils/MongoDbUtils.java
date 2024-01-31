package com.hcmute.shopfee.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class MongoDbUtils {
    public static Date createCurrentDateTime(int hour, int minute, int second, int millisecond) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime currentDate = now.withHour(hour).withMinute(minute).withSecond(second).withNano(millisecond);
        return Date.from(currentDate.toInstant());
    }
    public static Date createPreviousDay(int hour, int minute, int second, int millisecond, int prevDay) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime previousDay = now.minusDays(prevDay).withHour(hour).withMinute(minute).withSecond(second).withNano(millisecond);
        return Date.from(previousDay.toInstant());
    }

}
