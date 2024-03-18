package com.hcmute.shopfee.utils;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    public static Date createEndOfDate() {
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(Calendar.HOUR_OF_DAY, 23);
        calEnd.set(Calendar.MINUTE, 59);
        calEnd.set(Calendar.SECOND, 59);
        return calEnd.getTime();
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
    public static boolean isDateInRange(Date target, Date startPoint, Date endPoint) {
        if(endPoint == null) {
            return !target.before(startPoint);
        }
        return !target.before(startPoint) && !target.after(endPoint);
    }
    public static String getFormatTime(Time time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(time);
    }

    public static boolean isPassed30Minutes(Date createdAt) {
        // Lấy thời điểm hiện tại
        Date now = new Date();

        long diffInMilliseconds = now.getTime() - createdAt.getTime();
        long diffInMinutes = diffInMilliseconds / (60 * 1000);

        // Kiểm tra nếu khoảng thời gian lớn hơn hoặc bằng 30 phút
        return diffInMinutes > 30;
    }
    public static Time getCurrentTime(ZoneId zone) {
        LocalTime localTime = LocalTime.now(zone);
        return Time.valueOf(localTime);
    }

    public static Instant after(Instant original, int timeValue, ChronoUnit unit) {
        return original.plus(timeValue, unit);
    }
}
