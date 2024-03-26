package com.hcmute.shopfee.service.common;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.hcmute.shopfee.dto.common.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging;


    public String sendNotification(NotificationMessage notificationMessage) {
        Notification notification = Notification.builder()
                .setTitle(notificationMessage.getTitle())
                .setBody(notificationMessage.getBody())
                .setImage(notificationMessage.getImage())
                .build();

        Message message = Message.builder()
                .setToken(notificationMessage.getRecipientToken())
                .setNotification(notification)
                .putAllData(notificationMessage.getData())
                .build();

        try {
            firebaseMessaging.send(message);
            return "success FirebaseMessagingException";
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return "error FirebaseMessagingException";
        }
    }
}
