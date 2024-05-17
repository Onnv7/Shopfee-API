package com.hcmute.shopfee.module.zalopay;

import com.hcmute.shopfee.utils.DateUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class ZaloPayUtils {
    public static String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(DateUtils.GMT_7));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    public static String hmacSha256(String key, String data) {
        return Hex.encodeHexString(HmacUtils.hmacSha256(key.getBytes(), data.getBytes()));
    }
}
