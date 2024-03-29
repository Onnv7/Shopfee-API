package com.hcmute.shopfee.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic createEmailTopic() {
        return new NewTopic(KafkaConstant.SEND_CODE_EMAIL_TOPIC,  2, (short) 1);
    }
    @Bean
    public NewTopic createFirebaseFCMTopic() {
        return new NewTopic(KafkaConstant.FIREBASE_FCM_TOPIC,  3, (short) 1);
    }
}
