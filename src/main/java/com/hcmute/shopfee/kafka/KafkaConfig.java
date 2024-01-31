package com.hcmute.shopfee.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic createTopic() {
        return new NewTopic(KafkaConstant.SEND_CODE_EMAIL_TOPIC,  2, (short) 1);
    }
}
