package com.hcmute.shopfee.kafka.listener;

import com.hcmute.shopfee.entity.elasticsearch.RatingProductIndex;
import com.hcmute.shopfee.kafka.message.RatingProductMsgData;
import com.hcmute.shopfee.kafka.message.UserBlockedMsgData;
import com.hcmute.shopfee.repository.elasticsearch.RatingProductESRepository;
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
public class RatingProductKafkaListener {
    private final RatingProductESRepository ratingProductESRepository;

    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = RATING_PRODUCT_TOPIC, groupId = RATING_PRODUCT_GROUP_ID, id = RATING_PRODUCT_GROUP_ID + "-1")
    public void analystUserProductData1(RatingProductMsgData message) {
        log.info("Listener 1 consume {}", message.toString());
        RatingProductIndex data = RatingProductIndex.builder()
                .rating(message.getRating())
                .userId(message.getUserId())
                .productId(message.getProductId())
                .build();
        ratingProductESRepository.save(data);
    }

    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = RATING_PRODUCT_TOPIC, groupId = RATING_PRODUCT_GROUP_ID, id = RATING_PRODUCT_GROUP_ID + "-2")
    public void analystUserProductData2(RatingProductMsgData message) {
        log.info("Listener 1 consume {}", message.toString());
        RatingProductIndex data = RatingProductIndex.builder()
                .rating(message.getRating())
                .userId(message.getUserId())
                .productId(message.getProductId())
                .build();
        ratingProductESRepository.save(data);
    }

    @KafkaListener(topics = RATING_PRODUCT_TOPIC + "-dlt", groupId = RATING_PRODUCT_GROUP_ID, id = RATING_PRODUCT_GROUP_ID + "-dlt")
    public void consumeUserBlockedDataDLT(UserBlockedMsgData message) {
        log.error("Listener {} DLT consume =>>> {}", RATING_PRODUCT_GROUP_ID, message.toString());
    }
}
