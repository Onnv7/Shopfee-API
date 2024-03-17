package com.hcmute.shopfee.config;

import com.hcmute.shopfee.module.vnpay.VNPay;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VNPayConfig {
    @Value("${vnpay.secret_key}")
    private String SECRET_KEY;
    @Value("${vnpay.tmn_code}")
    private String TMN_CODE;

    @Bean
    public VNPay vnPay() {
        return new VNPay(SECRET_KEY, TMN_CODE);
    }
}
