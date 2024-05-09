package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.UpsertEmployeeFcmTokenRequest;
import com.hcmute.shopfee.dto.request.UpsertUserFcmTokenRequest;
import com.hcmute.shopfee.dto.response.UpsertFcmTokenResponse;

public interface INotificationService {
    UpsertFcmTokenResponse upsertUserFcmToken(UpsertUserFcmTokenRequest body);
    UpsertFcmTokenResponse upsertEmployeeFcmToken(UpsertEmployeeFcmTokenRequest body);
}
