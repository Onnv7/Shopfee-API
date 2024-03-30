package com.hcmute.shopfee.kafka.publisher;

import com.hcmute.shopfee.dto.kafka.CodeEmailDto;
import com.hcmute.shopfee.kafka.KafkaConstant;
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

    public void sendMessageToCodeEmail(CodeEmailDto message) {
        CompletableFuture<SendResult<String, Object>> future = template.send(KafkaConstant.SEND_CODE_EMAIL_TOPIC, message);
        future.whenComplete((rs, ex) -> {
            if(ex == null) {
                log.info("MailerKafkaPublisher: Topic = {}, Partition = {}, Offset = {}, Message = {}", rs.getRecordMetadata().topic(),
                        rs.getRecordMetadata().partition(), rs.getRecordMetadata().offset(), rs.getProducerRecord().value());
            } else {
                log.error("MailerKafkaPublisher error {}", ex.getMessage());
            }
        });
    }
}
