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

@Service
@RequiredArgsConstructor
public class FirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging;
    private final UserFCMTokenRepository userFCMTokenEntityRepository;


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
}
