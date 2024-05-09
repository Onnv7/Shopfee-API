package com.hcmute.shopfee.kafka.publisher;

import com.hcmute.shopfee.kafka.message.CodeEmailMsgData;
import com.hcmute.shopfee.kafka.KafkaConstant;
import com.hcmute.shopfee.kafka.message.UserBlockedMsgData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MailerKafkaPublisher {
    @Autowired
    private KafkaTemplate<String, Object> template;

    public void sendMessageToCodeEmail(CodeEmailMsgData message) {
        CompletableFuture<SendResult<String, Object>> future = template.send(KafkaConstant.SEND_CODE_EMAIL_TOPIC, message);
        future.whenComplete((rs, ex) -> {
            if(ex == null) {
                log.info("Publisher: Topic = {}, Partition = {}, Offset = {}, Message = {}", rs.getRecordMetadata().topic(),
                        rs.getRecordMetadata().partition(), rs.getRecordMetadata().offset(), rs.getProducerRecord().value());
            } else {
                log.error("Publisher error {}", ex.getMessage());
            }
        });
    }

    public void sendBlockedStatus(UserBlockedMsgData message) {
        CompletableFuture<SendResult<String, Object>> future = template.send(KafkaConstant.SEND_USER_BLOCKED_EMAIL_TOPIC, message);
        future.whenComplete((rs, ex) -> {
            if(ex == null) {
                log.info("Publisher: Topic = {}, Partition = {}, Offset = {}, Message = {}", rs.getRecordMetadata().topic(),
                        rs.getRecordMetadata().partition(), rs.getRecordMetadata().offset(), rs.getProducerRecord().value());
            } else {
                log.error("Publisher error {}", ex.getMessage());
            }
        });
    }
}
