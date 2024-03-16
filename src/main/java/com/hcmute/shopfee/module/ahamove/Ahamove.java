package com.hcmute.shopfee.module.ahamove;

public class Ahamove {
    private static String AHAMOVE_TOKEN;

    public static String getAhamoveToken() {
        return AHAMOVE_TOKEN;
    }

    public Ahamove(String AHAMOVE_TOKEN) {
        Ahamove.AHAMOVE_TOKEN = AHAMOVE_TOKEN;
    }
}
