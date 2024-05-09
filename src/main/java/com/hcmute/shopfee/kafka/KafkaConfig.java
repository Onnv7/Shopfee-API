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
    public NewTopic createUserOrderNotification() {
        return new NewTopic(KafkaConstant.USER_ORDER_NOTIFICATION_TOPIC,  4, (short) 1);
    }

    @Bean
    public NewTopic createEmployeeOOrderNotificationTopic() {
        return new NewTopic(KafkaConstant.EMPLOYEE_ORDER_NOTIFICATION_TOPIC,  4, (short) 1);
    }
    @Bean
    public NewTopic createUserBlockedEmail() {
        return new NewTopic(KafkaConstant.SEND_USER_BLOCKED_EMAIL_TOPIC,  2, (short) 1);
    }
}
