package com.hcmute.shopfee.utils;

public class StringUtils {
    public static String removeNonAlphaNumeric(String input) {
        String regex = "[^a-zA-Z0-9]";
        return input.replaceAll(regex, "");
    }

    public static String generateFileName(String nameRaw, String postfix) {
        return removeNonAlphaNumeric(nameRaw) + "_" + postfix;
    }
}
