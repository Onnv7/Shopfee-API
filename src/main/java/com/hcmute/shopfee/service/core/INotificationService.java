package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.CreateEmployeeFcmTokenRequest;
import com.hcmute.shopfee.dto.request.CreateUserFcmTokenRequest;
import com.hcmute.shopfee.dto.request.UpdateFcmTokenRequest;
import com.hcmute.shopfee.dto.response.CreateFcmTokenResponse;

public interface INotificationService {
    CreateFcmTokenResponse createUserFcmToken(CreateUserFcmTokenRequest body);
    CreateFcmTokenResponse createEmployeeFcmToken(CreateEmployeeFcmTokenRequest body);
    void updateUserFcmToken(UpdateFcmTokenRequest body);
}
