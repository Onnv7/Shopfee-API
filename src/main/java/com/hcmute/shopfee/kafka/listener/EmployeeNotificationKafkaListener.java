package com.hcmute.shopfee.kafka.listener;

import com.hcmute.shopfee.kafka.message.OrderStatusMsgData;
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
public class EmployeeNotificationKafkaListener {
    private final FirebaseMessagingService firebaseMessagingService;

    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = EMPLOYEE_ORDER_NOTIFICATION_TOPIC, groupId = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID, id = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID + "-1")
    public void consumeOrderBranchNotification1(OrderStatusMsgData message) {
        log.info("Listener 1 consume {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToUser(message);
    }

    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = EMPLOYEE_ORDER_NOTIFICATION_TOPIC, groupId = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID, id = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID + "-2")
    public void consumeOrderBranchNotification2(OrderStatusMsgData message) {
        log.info("Listener 2 consume {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToUser(message);
    }

    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = EMPLOYEE_ORDER_NOTIFICATION_TOPIC, groupId = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID, id = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID + "-3")
    public void consumeOrderBranchNotification3(OrderStatusMsgData message) {
        log.info("Listener 3 consume {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToUser(message);
    }

    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = EMPLOYEE_ORDER_NOTIFICATION_TOPIC, groupId = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID, id = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID + "-4")
    public void consumeOrderBranchNotification4(OrderStatusMsgData message) {
        log.info("Listener 4 consume {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToUser(message);
    }

    @KafkaListener(topics = EMPLOYEE_ORDER_NOTIFICATION_TOPIC + "-dlt", groupId = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID, id = EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID + "-dlt")
    public void consumeSendCodeEmailDLT(OrderStatusMsgData message) {
        log.error("Listener {} DLT consume DLT =>>> {}", EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID, message.toString());
    }
}
