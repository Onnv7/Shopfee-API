package com.hcmute.shopfee.utils;

import java.util.Date;

public class StringUtils {
    public static String removeNonAlphaNumericAndShortCutText(String input) {
        String regex = "[^a-zA-Z0-9]";
        input = input.replaceAll(regex, "");
        return input.substring(0, Math.min(input.length() - 1, 30));
    }

    public static String generateFileNameByTime(String nameRaw, String postfix) {
        if (nameRaw == null || nameRaw.isBlank()) {
            return postfix + "_" + new Date().getTime();
        }
        return removeNonAlphaNumericAndShortCutText(nameRaw) + "_" + postfix + "_" + new Date().getTime();
    }
    public static String generateFileNameById(String id, String prefix) {
        return prefix +  "_" + removeNonAlphaNumericAndShortCutText(id);
    }
}
