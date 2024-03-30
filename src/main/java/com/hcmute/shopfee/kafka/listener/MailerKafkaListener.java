package com.hcmute.shopfee.kafka.listener;

import com.hcmute.shopfee.dto.kafka.CodeEmailDto;
import com.hcmute.shopfee.utils.EmailUtils;
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
public class MailerKafkaListener {
    private final EmailUtils emailUtils;


    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = SEND_CODE_EMAIL_TOPIC, groupId = SEND_EMAIL_CONSUMER_GROUP_ID, id = "1")
    public void consumeSendCodeEmail1(CodeEmailDto message) {
        log.info("MailerKafkaListener Consumer sending1 {}", message.toString());
        emailUtils.sendHtmlVerifyCodeToRegister(message.getEmail(), message.getCode());
    }

    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = SEND_CODE_EMAIL_TOPIC, groupId = SEND_EMAIL_CONSUMER_GROUP_ID, id = "2")
    public void consumeSendCodeEmail2(CodeEmailDto message) {
        log.info("MailerKafkaListener Consumer sending2 {}", message.toString());
        emailUtils.sendHtmlVerifyCodeToRegister(message.getEmail(), message.getCode());
    }
    @KafkaListener(groupId = "receive_email", topics =  SEND_CODE_EMAIL_TOPIC + "-dlt")
    public void consumeSendCodeEmailDLT(CodeEmailDto message) {
        log.info("MailerKafkaListener DLT =>>> {}", message.toString());
        emailUtils.sendHtmlVerifyCodeToRegister(message.getEmail(), message.getCode());
    }
}
