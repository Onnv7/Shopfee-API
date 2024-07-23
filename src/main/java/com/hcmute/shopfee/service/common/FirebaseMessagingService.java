package com.hcmute.shopfee.service.common;

import com.google.firebase.messaging.*;
import com.hcmute.shopfee.constant.ShopfeeConstant;
import com.hcmute.shopfee.entity.sql.database.SystemNotificationEntity;
import com.hcmute.shopfee.kafka.message.NewOrderMsgData;
import com.hcmute.shopfee.kafka.message.OrderStatusMsgData;
import com.hcmute.shopfee.entity.sql.database.user.UserFCMTokenEntity;
import com.hcmute.shopfee.repository.database.UserFCMTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging;
    private final UserFCMTokenRepository userFCMTokenEntityRepository;

    public void sendOrderNotificationToBranch(NewOrderMsgData msg) {
        Notification notification = Notification.builder()
                .setTitle(msg.getTitle())
                .setBody(msg.getBody())
                .build();
        Message message = Message.builder()
                .setTopic(msg.getBranchId())
                .setNotification(notification)
                .putAllData(msg.getData())
                .build();
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }
    public void sendOrderNotificationToUser(OrderStatusMsgData msg) {
        List<UserFCMTokenEntity> userFCMTokenEntityList = userFCMTokenEntityRepository.findByUser_Id(msg.getClientId());
        Notification notification = Notification.builder()
                .setTitle(msg.getTitle())
                .setBody(msg.getBody())
                .build();
        for(UserFCMTokenEntity entity: userFCMTokenEntityList) {
            Message message = Message.builder()
                    .setToken(entity.getToken())
                    .putAllData(msg.getData())
                    .setNotification(notification)
                    .build();
            try {
                firebaseMessaging.send(message);
            } catch (FirebaseMessagingException e) {
                userFCMTokenEntityRepository.delete(entity);
            }
        }
    }

    public void sendSystemNotification(SystemNotificationEntity msg) {
        Notification notification = Notification.builder()
                .setTitle(msg.getTitle())
                .setBody(msg.getContent())
                .build();
        Message message = Message.builder()
                .setTopic(ShopfeeConstant.SYSTEM_FCM_TOPIC)
                .setNotification(notification)
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }
}
