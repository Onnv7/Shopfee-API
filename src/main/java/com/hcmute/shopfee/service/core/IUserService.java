package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.payload.request.*;
import com.hcmute.shopfee.enums.param.UserChartStatisticType;
import com.hcmute.shopfee.enums.UserStatus;
import com.hcmute.shopfee.payload.response.*;

import java.sql.Date;

public interface IUserService {
    GetAllUserResponse getUserList(String key, UserStatus status, int page, int size);
    GetUserByIdResponse getUserProfileById(String userId);
    GetUserDetailsByIdResponse getUserDetail(String userId);
    void changeUserStatus(String userId, UserStatus status);
    void updateUserProfile(String userId, UpdateUserRequest body);
    String checkExistedUserByEmail(String email);
    UploadAvatarResponse uploadAvatar(UploadUserAvatarRequest body, String userId);
    void addPhoneNumberToUser(AddPhoneNumberRequest body, String userId);
    GetUserSpendingStatisticsResponse getUserSpendingStatistic(String userId, Date startDate, Date endDate);
    GetUserOrderStatusStatisticsResponse getOrderStatisticByUserId(String userId, UserChartStatisticType chartType);
    GetCoinHistoryListResponse getCoinHistoryList(String userId, int page, int size);
}
