package com.hcmute.shopfee.service.common;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.hcmute.shopfee.dto.common.NotificationMessageDto;
import com.hcmute.shopfee.dto.common.OrderNotificationDto;
import com.hcmute.shopfee.entity.sql.database.UserFCMTokenEntity;
import com.hcmute.shopfee.repository.database.UserFCMTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging;
    private final UserFCMTokenRepository userFCMTokenEntityRepository;
    public static final String TITLE_NOTI_KEY = "title";
    public static final String BODY_NOTI_KEY = "body";
    public static final String CLIENT_ID_NOTI_KEY = "client_id";

    public void sendOrderNotificationToBranch(String branchId, String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
        Message message = Message.builder()
                .setTopic(branchId)
                .setNotification(notification)
                .build();
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }
    public void sendOrderNotificationToUser(OrderNotificationDto notificationDto) {
        List<UserFCMTokenEntity> userFCMTokenEntityList = userFCMTokenEntityRepository.findByUser_Id(notificationDto.getClientId());
        Notification notification = Notification.builder()
                .setTitle(notificationDto.getTitle())
                .setBody(notificationDto.getBody())
                .build();
        for(UserFCMTokenEntity entity: userFCMTokenEntityList) {
            Message message = Message.builder()
                    .setToken(entity.getToken())
                    .setNotification(notification)
                    .build();
            try {
                firebaseMessaging.send(message);
            } catch (FirebaseMessagingException e) {
                userFCMTokenEntityRepository.delete(entity);
            }
        }
    }

    public void sendOrderNotificationToUser(Map<String, Object> message) {
        List<UserFCMTokenEntity> userFCMTokenEntityList = userFCMTokenEntityRepository.findByUser_Id(message.get(CLIENT_ID_NOTI_KEY).toString());
        Notification notification = Notification.builder()
                .setTitle("Shopfee")
                .setBody(message.get(BODY_NOTI_KEY).toString())
                .build();
        for(UserFCMTokenEntity entity: userFCMTokenEntityList) {
            Message msg = Message.builder()
                    .setToken(entity.getToken())
                    .setNotification(notification)
                    .build();
            try {
                firebaseMessaging.send(msg);
            } catch (FirebaseMessagingException e) {
                userFCMTokenEntityRepository.delete(entity);
            }
        }
    }
}
