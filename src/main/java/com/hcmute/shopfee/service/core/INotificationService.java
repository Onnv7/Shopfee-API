package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.payload.request.UpsertNotificationFCMRequest;
import com.hcmute.shopfee.payload.request.UpsertEmployeeFcmTokenRequest;
import com.hcmute.shopfee.payload.request.UpsertUserFcmTokenRequest;
import com.hcmute.shopfee.payload.response.GetNotificationList;
import com.hcmute.shopfee.payload.response.GetSystemNotificationDetailResponse;
import com.hcmute.shopfee.payload.response.UpsertFcmTokenResponse;

public interface INotificationService {
    UpsertFcmTokenResponse upsertUserFcmToken(UpsertUserFcmTokenRequest body);
    UpsertFcmTokenResponse upsertEmployeeFcmToken(UpsertEmployeeFcmTokenRequest body);
    void createNotification(UpsertNotificationFCMRequest body);
    void updateNotification(String notificationId, UpsertNotificationFCMRequest body);
    GetNotificationList getNotificationList(int page, int size);
    GetSystemNotificationDetailResponse getNotificationDetailsById(String notificationId);
}
