package com.hcmute.shopfee.kafka.listener;

import com.hcmute.shopfee.entity.elasticsearch.TrackingUserProductIndex;
import com.hcmute.shopfee.kafka.message.TrackingUserProductMsgData;
import com.hcmute.shopfee.kafka.message.UserBlockedMsgData;
import com.hcmute.shopfee.repository.elasticsearch.TrackingUserClickProductESRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.hcmute.shopfee.kafka.KafkaConstant.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrackingUserProductKafkaListener {
    private final TrackingUserClickProductESRepository trackingUserClickProductESRepository;

    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = TRACKING_USER_PRODUCT_TOPIC, groupId = TRACKING_USER_PRODUCT_GROUP_ID, id = TRACKING_USER_PRODUCT_GROUP_ID + "-1")
    public void analystUserProductData(TrackingUserProductMsgData message) {
        log.info("Listener 1 consume {}", message.toString());
        TrackingUserProductIndex data = trackingUserClickProductESRepository.findByUserIdAndProductId(message.getUserId(), message.getProductId())
                .orElse(null);
        if (data == null) {
            data = new TrackingUserProductIndex();
            data.setProductId(message.getProductId());
            data.setUserId(message.getUserId());
            data.setClickCount(1);
            data.setLastSeen(new Date());
        } else {
            data.setClickCount(data.getClickCount() + 1);
            data.setLastSeen(new Date());
        }
        trackingUserClickProductESRepository.save(data);
    }

    @KafkaListener(topics = TRACKING_USER_PRODUCT_TOPIC + "-dlt", groupId = TRACKING_USER_PRODUCT_GROUP_ID, id = TRACKING_USER_PRODUCT_GROUP_ID + "-dlt")
    public void consumeUserBlockedDataDLT(UserBlockedMsgData message) {
        log.error("Listener {} DLT consume =>>> {}", TRACKING_USER_PRODUCT_GROUP_ID, message.toString());
    }
}
