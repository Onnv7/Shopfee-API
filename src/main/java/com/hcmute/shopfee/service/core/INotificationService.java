package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.UpsertNotificationFCMRequest;
import com.hcmute.shopfee.dto.request.UpsertEmployeeFcmTokenRequest;
import com.hcmute.shopfee.dto.request.UpsertUserFcmTokenRequest;
import com.hcmute.shopfee.dto.response.GetNotificationList;
import com.hcmute.shopfee.dto.response.GetSystemNotificationDetailResponse;
import com.hcmute.shopfee.dto.response.UpsertFcmTokenResponse;

public interface INotificationService {
    UpsertFcmTokenResponse upsertUserFcmToken(UpsertUserFcmTokenRequest body);
    UpsertFcmTokenResponse upsertEmployeeFcmToken(UpsertEmployeeFcmTokenRequest body);
    void createNotification(UpsertNotificationFCMRequest body);
    void updateNotification(String notificationId, UpsertNotificationFCMRequest body);
    GetNotificationList getNotificationList(int page, int size);
    GetSystemNotificationDetailResponse getNotificationDetailsById(String notificationId);
}
