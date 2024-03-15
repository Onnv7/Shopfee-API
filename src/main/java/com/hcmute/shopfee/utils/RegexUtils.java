package com.hcmute.shopfee.utils;
public class RegexUtils {
    public static String generateFilterRegexString(String filter) {
        filter = filter.toLowerCase();
        return ".*" + filter + ".*";
    }
}
