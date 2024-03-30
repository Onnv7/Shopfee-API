package com.hcmute.shopfee.kafka.listener;

import com.hcmute.shopfee.dto.common.OrderNotificationDto;
import com.hcmute.shopfee.dto.kafka.BranchNotificationDto;
import com.hcmute.shopfee.service.common.FirebaseMessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import static com.hcmute.shopfee.kafka.KafkaConstant.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeOrderNotificationKafkaListener {
    private final FirebaseMessagingService firebaseMessagingService;
    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = EMPLOYEE_ORDER_NOTIFICATION_TOPIC, groupId = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID, id = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID + "-1")
    public void consumeOrderBranchNotification1(OrderNotificationDto message) {
        log.info("EmployeeOrderNotificationKafkaListener 1 Consumer sending {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToUser(message);
    }
    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = EMPLOYEE_ORDER_NOTIFICATION_TOPIC, groupId = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID, id = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID + "-2")
    public void consumeOrderBranchNotification2(OrderNotificationDto message) {
        log.info("EmployeeOrderNotificationKafkaListener 2 Consumer sending {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToUser(message);
    }
    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = EMPLOYEE_ORDER_NOTIFICATION_TOPIC, groupId = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID, id = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID + "-3")
    public void consumeOrderBranchNotification3(OrderNotificationDto message) {
        log.info("EmployeeOrderNotificationKafkaListener 3 Consumer sending {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToUser(message);
    }
    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = EMPLOYEE_ORDER_NOTIFICATION_TOPIC, groupId = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID, id = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID + "-4")
    public void consumeOrderBranchNotification4(OrderNotificationDto message) {
        log.info("EmployeeOrderNotificationKafkaListener 4 Consumer sending {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToUser(message);
    }
}
