package com.hcmute.shopfee.utils;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Random;

@Component
public class RandomUtils {
    public static String randomTimeId() {
        Random random = new Random();
        Date date = new Date();
        return date.getTime() + String.format("%02d", random.nextInt(100));
    }

    public static String generateRandomCode(int length) {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int randomNumber = random.nextInt(10);
            code.append(randomNumber);
        }

        return code.toString();
    }
}
