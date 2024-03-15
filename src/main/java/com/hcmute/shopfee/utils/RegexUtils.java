package com.hcmute.shopfee.utils;
public class RegexUtils {
    public static String generateFilterRegexString(String filter) {
        filter = filter.toLowerCase();
        return ".*" + filter + ".*";
    }
    public static String generateStringLikeSql(String filter) {
        return "%" + filter + "%";
    }
}
