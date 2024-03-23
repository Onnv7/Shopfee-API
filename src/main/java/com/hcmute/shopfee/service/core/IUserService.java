package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.AddPhoneNumberRequest;
import com.hcmute.shopfee.dto.request.UpdateUserRequest;
import com.hcmute.shopfee.dto.request.UploadUserAvatarRequest;
import com.hcmute.shopfee.dto.response.GetAllUserResponse;
import com.hcmute.shopfee.dto.response.GetUserByIdResponse;
import com.hcmute.shopfee.enums.UserStatus;

public interface IUserService {
    GetAllUserResponse getUserList(String key, UserStatus status, int page, int size);
    GetUserByIdResponse getUserProfileById(String userId);
    void updateUserProfile(String userId, UpdateUserRequest body);
    String checkExistedUserByEmail(String email);
    void uploadAvatar(UploadUserAvatarRequest body, String userId);
    void addPhoneNumberToUser(AddPhoneNumberRequest body, String userId);
}
