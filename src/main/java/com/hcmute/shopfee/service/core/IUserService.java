package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.AddPhoneNumberRequest;
import com.hcmute.shopfee.dto.request.UpdateUserRequest;
import com.hcmute.shopfee.dto.request.UploadUserAvatarRequest;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.enums.UserChartStatisticType;
import com.hcmute.shopfee.enums.UserStatus;

import java.sql.Date;

public interface IUserService {
    GetAllUserResponse getUserList(String key, UserStatus status, int page, int size);
    GetUserByIdResponse getUserProfileById(String userId);
    void updateUserProfile(String userId, UpdateUserRequest body);
    String checkExistedUserByEmail(String email);
    UploadAvatarResponse uploadAvatar(UploadUserAvatarRequest body, String userId);
    void addPhoneNumberToUser(AddPhoneNumberRequest body, String userId);
    GetUserSpendingStatisticsResponse getUserSpendingStatistic(String userId, Date startDate, Date endDate);
    GetUserOrderStatusStatisticsResponse getOrderStatisticByUserId(String userId, UserChartStatisticType chartType);
    GetCoinHistoryListResponse getCoinHistoryList(String userId, int page, int size);
}
