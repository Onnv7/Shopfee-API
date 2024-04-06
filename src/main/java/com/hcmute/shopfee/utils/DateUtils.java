package com.hcmute.shopfee.utils;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static Date createBeginingOfDate() {
        Calendar calBegin = Calendar.getInstance();
        calBegin.set(Calendar.HOUR_OF_DAY, 0);
        calBegin.set(Calendar.MINUTE, 0);
        calBegin.set(Calendar.SECOND, 0);
        return calBegin.getTime();
    }

    public static String formatYYYYMMDD(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return  sdf.format(date);
    }
    public static String formatHHmm(Time time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(time);
    }
    public static Date createDateTimeByToday(int hour, int minute, int second, int millisecond, int numberDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, numberDate);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar.getTime();
    }

    public static String getFormatTime(Time time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(time);
    }


    public static Time getCurrentTime(ZoneId zone) {
        LocalTime localTime = LocalTime.now(zone);
        return Time.valueOf(localTime);
    }

    public static Instant plus(Instant original, int timeValue, ChronoUnit unit) {
        return original.plus(timeValue, unit);
    }
}
