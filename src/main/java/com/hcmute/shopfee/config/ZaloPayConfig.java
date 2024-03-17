package com.hcmute.shopfee.config;

import com.hcmute.shopfee.module.zalopay.ZaloPay;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZaloPayConfig {
    @Value("${zalo_pay.app_id}")
    private String APP_ID;
    @Value("${zalo_pay.key1}")
    private String KEY1;
    @Value("${zalo_pay.key2}")
    private String KEY2;

    @Bean
    public ZaloPay zaloPay() {
        return new ZaloPay(APP_ID, KEY1, KEY2);
    }
}
