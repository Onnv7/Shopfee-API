package com.hcmute.shopfee.kafka.listener;

import com.hcmute.shopfee.kafka.message.NewOrderMsgData;
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
public class UserNotificationKafkaListener {
    private final FirebaseMessagingService firebaseMessagingService;
    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = USER_ORDER_NOTIFICATION_TOPIC, groupId = USER_ORDER_NOTIFICATION_GROUP_ID, id = USER_ORDER_NOTIFICATION_GROUP_ID + "-1")
    public void consumeOrderBranchNotification1(NewOrderMsgData message) {
        log.info("Listener 1 consume {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToBranch(message);
    }

    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = USER_ORDER_NOTIFICATION_TOPIC, groupId = USER_ORDER_NOTIFICATION_GROUP_ID, id = USER_ORDER_NOTIFICATION_GROUP_ID + "-2")
    public void consumeOrderBranchNotification2(NewOrderMsgData message) {
        log.info("Listener 2 consume {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToBranch(message);
    }
    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = USER_ORDER_NOTIFICATION_TOPIC, groupId = USER_ORDER_NOTIFICATION_GROUP_ID, id = USER_ORDER_NOTIFICATION_GROUP_ID + "-3")
    public void consumeOrderBranchNotification3(NewOrderMsgData message) {
        log.info("Listener 3 consume {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToBranch(message);
    }
    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = USER_ORDER_NOTIFICATION_TOPIC, groupId = USER_ORDER_NOTIFICATION_GROUP_ID, id = USER_ORDER_NOTIFICATION_GROUP_ID + "-4")
    public void consumeOrderBranchNotification4(NewOrderMsgData message) {
        log.info("Listener 4 consume {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToBranch(message);
    }

    @KafkaListener(groupId = USER_ORDER_NOTIFICATION_GROUP_ID, topics =  USER_ORDER_NOTIFICATION_TOPIC + "-dlt")
    public void consumeSendCodeEmailDLT(NewOrderMsgData message) {
        log.info("Listener DLT consume DLT =>>> {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToBranch(message);
    }
}
