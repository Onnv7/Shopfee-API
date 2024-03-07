package com.hcmute.shopfee.config;

import com.hcmute.shopfee.module.goong.Goong;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoongConfig {
    @Value("${goong.api_key}")
    private  String API_KEY;

    @Bean
    public Goong goong() {
        return new Goong(API_KEY);
    }

}
