package com.hcmute.shopfee.utils;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
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
    public static boolean isWithin31Days(java.sql.Date startDate, java.sql.Date endDate) {
        LocalDate startLocalDate = startDate.toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();

        // Tính khoảng thời gian giữa startDate và endDate
        Duration duration = Duration.between(startLocalDate.atStartOfDay(), endLocalDate.atStartOfDay());

        return duration.toDays() <= 31;
    }
    public static java.sql.Date getSqlDateFromTimeString(String timeString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = sdf.parse(timeString);
            return new java.sql.Date(utilDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace(); // Xử lý ngoại lệ nếu có
            return null; // Trả về null nếu không thể chuyển đổi
        }
    }

    public static boolean nowIsAfterPeriodFromTimeOriginal(Instant timeOriginal, int period, ChronoUnit unit) {
        Instant timeAfterPeriod = timeOriginal.plus(period, unit);
        return Instant.now().isAfter(timeAfterPeriod);
    }
}
