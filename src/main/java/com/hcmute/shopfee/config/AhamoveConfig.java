package com.hcmute.shopfee.config;

import com.hcmute.shopfee.module.ahamove.Ahamove;
import com.hcmute.shopfee.module.goong.Goong;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AhamoveConfig {
    @Value("${ahamove.token}")
    private  String AHAMOVE_TOKEN;

    @Bean
    public Ahamove ahamove() {
        return new Ahamove(AHAMOVE_TOKEN);
    }
}
