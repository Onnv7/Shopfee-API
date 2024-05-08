package com.hcmute.shopfee.service.common;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.hcmute.shopfee.kafka.message.NewOrderMsgData;
import com.hcmute.shopfee.kafka.message.OrderStatusMsgData;
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

    public void sendOrderNotificationToBranch(NewOrderMsgData msg) {
        Notification notification = Notification.builder()
                .setTitle(msg.getTitle())
                .setBody(msg.getBody())
                .build();
        Message message = Message.builder()
                .setTopic(msg.getBranchId())
                .setNotification(notification)
                .build();
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
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
