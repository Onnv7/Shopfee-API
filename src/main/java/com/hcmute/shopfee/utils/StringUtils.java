package com.hcmute.shopfee.utils;

import java.util.Date;

public class StringUtils {
    public static String removeNonAlphaNumeric(String input) {
        String regex = "[^a-zA-Z0-9]";
        return input.replaceAll(regex, "");
    }

    public static String generateFileName(String nameRaw, String postfix) {
        if (nameRaw == null || nameRaw.isBlank()) {
            return postfix + "_" + new Date().getTime();
        }
        return removeNonAlphaNumeric(nameRaw) + "_" + postfix + "_" + new Date().getTime();
    }
}
