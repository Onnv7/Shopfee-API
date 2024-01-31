package com.hcmute.shopfee.utils;

import java.util.Random;

public class GeneratorUtils {
    public static String generateRandomCode(int length) {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int randomNumber = random.nextInt(10);
            code.append(randomNumber);
        }

        return code.toString();
    }
    public static String formatCodeItem(long number, String prefix, int lengthNumber) {
        String formatString = "%0" + lengthNumber + "d";
        String formattedNumber = String.format(formatString, number);
        return prefix + formattedNumber;
    }
}
