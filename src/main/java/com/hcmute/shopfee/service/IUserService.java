package com.hcmute.shopfee.service;

import com.hcmute.shopfee.dto.request.UpdateUserRequest;
import com.hcmute.shopfee.dto.response.GetAllUserResponse;
import com.hcmute.shopfee.dto.response.GetUserByIdResponse;

public interface IUserService {
    GetAllUserResponse getUserList(int page, int size);
    GetUserByIdResponse getUserProfileById(String userId);
    void updateUserProfile(String userId, UpdateUserRequest body);
    String checkExistedUserByEmail(String email);
}
