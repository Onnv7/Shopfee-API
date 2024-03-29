package com.hcmute.shopfee.kafka;

import com.hcmute.shopfee.dto.kafka.BranchNotificationDto;
import com.hcmute.shopfee.dto.kafka.CodeEmailDto;
import com.hcmute.shopfee.service.common.FirebaseMessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import static com.hcmute.shopfee.kafka.KafkaConstant.SEND_CODE_EMAIL_TOPIC;
import static com.hcmute.shopfee.kafka.KafkaConstant.SEND_EMAIL_CONSUMER;

@Service
@Slf4j
@RequiredArgsConstructor
public class FirebaseFCMListener {
    private final FirebaseMessagingService firebaseMessagingService;
    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = SEND_CODE_EMAIL_TOPIC, groupId = SEND_EMAIL_CONSUMER, id = "3")
    public void consumeOrderBranchNotification(BranchNotificationDto message) {
        log.info("Kafka Consumer sending1 {}", message.toString());
        firebaseMessagingService.sendOrderNotificationToBranch(message.getBranchId(), message.getTitle(), message.getBody());
    }
}
