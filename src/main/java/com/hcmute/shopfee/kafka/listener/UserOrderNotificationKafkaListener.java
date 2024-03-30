package com.hcmute.shopfee.kafka.listener;

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
public class UserOrderNotificationKafkaListener {
    private final FirebaseMessagingService firebaseMessagingService;
    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = USER_ORDER_NOTIFICATION_TOPIC, groupId = USER_ORDER_NOTIFICATION_GROUP_ID, id = USER_ORDER_NOTIFICATION_GROUP_ID + "-1")
    public void consumeOrderBranchNotification1(BranchNotificationDto message) {
        log.info("UserOrderNotificationKafkaListener 1 Consumer sending {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToBranch(message.getBranchId(), message.getTitle(), message.getBody());
    }

    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = USER_ORDER_NOTIFICATION_TOPIC, groupId = USER_ORDER_NOTIFICATION_GROUP_ID, id = USER_ORDER_NOTIFICATION_GROUP_ID + "-2")
    public void consumeOrderBranchNotification2(BranchNotificationDto message) {
        log.info("UserOrderNotificationKafkaListener 2 Consumer sending {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToBranch(message.getBranchId(), message.getTitle(), message.getBody());
    }
    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = USER_ORDER_NOTIFICATION_TOPIC, groupId = USER_ORDER_NOTIFICATION_GROUP_ID, id = USER_ORDER_NOTIFICATION_GROUP_ID + "-3")
    public void consumeOrderBranchNotification3(BranchNotificationDto message) {
        log.info("UserOrderNotificationKafkaListener 3 Consumer sending {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToBranch(message.getBranchId(), message.getTitle(), message.getBody());
    }
    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = USER_ORDER_NOTIFICATION_TOPIC, groupId = USER_ORDER_NOTIFICATION_GROUP_ID, id = USER_ORDER_NOTIFICATION_GROUP_ID + "-4")
    public void consumeOrderBranchNotification4(BranchNotificationDto message) {
        log.info("UserOrderNotificationKafkaListener 4 Consumer sending {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToBranch(message.getBranchId(), message.getTitle(), message.getBody());
    }

    @KafkaListener(groupId = USER_ORDER_NOTIFICATION_GROUP_ID, topics =  USER_ORDER_NOTIFICATION_TOPIC + "-dlt")
    public void consumeSendCodeEmailDLT(BranchNotificationDto message) {
        log.info("UserOrderNotificationKafkaListener DLT =>>> {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToBranch(message.getBranchId(), message.getTitle(), message.getBody());
    }
}
