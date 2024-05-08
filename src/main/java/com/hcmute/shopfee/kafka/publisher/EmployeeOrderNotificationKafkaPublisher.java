package com.hcmute.shopfee.kafka.publisher;

import com.hcmute.shopfee.dto.common.OrderNotificationDto;
import com.hcmute.shopfee.kafka.KafkaConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class EmployeeOrderNotificationKafkaPublisher {
    @Autowired
    private KafkaTemplate<String, Object> template;

    public void sendNotificationToUserId(OrderNotificationDto message) {
        CompletableFuture<SendResult<String, Object>> future = template.send(KafkaConstant.EMPLOYEE_ORDER_NOTIFICATION_TOPIC, message);
        future.whenComplete((rs, ex) -> {
            if (ex == null) {
                log.info("EmployeeOrderNotificationKafkaPublisher: Topic = {}, Partition = {}, Offset = {}, Message = {}", rs.getRecordMetadata().topic(),
                        rs.getRecordMetadata().partition(), rs.getRecordMetadata().offset(), rs.getProducerRecord().value());
            } else {
                log.error("EmployeeOrderNotificationKafkaPublisher error {}", ex.getMessage());
            }
        });
    }

    public void sendNotificationToUserId(Map<String, Object> message) {
        CompletableFuture<SendResult<String, Object>> future = template.send(KafkaConstant.EMPLOYEE_ORDER_NOTIFICATION_TOPIC, message);
        future.whenComplete((rs, ex) -> {
            if (ex == null) {
                log.info("EmployeeOrderNotificationKafkaPublisher: Topic = {}, Partition = {}, Offset = {}, Message = {}", rs.getRecordMetadata().topic(),
                        rs.getRecordMetadata().partition(), rs.getRecordMetadata().offset(), rs.getProducerRecord().value());
            } else {
                log.error("EmployeeOrderNotificationKafkaPublisher error {}", ex.getMessage());
            }
        });
    }
}
