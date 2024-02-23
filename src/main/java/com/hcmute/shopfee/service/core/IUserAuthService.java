package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.RegisterUserRequest;
import com.hcmute.shopfee.dto.request.UpdatePasswordRequest;
import com.hcmute.shopfee.dto.response.LoginResponse;
import com.hcmute.shopfee.dto.response.RefreshTokenResponse;
import com.hcmute.shopfee.dto.response.RegisterResponse;

public interface IUserAuthService {
    RegisterResponse registerUser(RegisterUserRequest body);
    LoginResponse userLogin(String email, String password);
    void logoutUser(String refreshToken);
    void resendCode(String email);
    void sendCodeToRegister(String email);
    void sendCodeToGetPassword(String email);
    void verifyCodeByEmail(String code, String email);
    void changePasswordForgot(String email, String password);
    RefreshTokenResponse refreshToken(String refreshToken);
    void changePasswordProfile(String userId, UpdatePasswordRequest data);
}