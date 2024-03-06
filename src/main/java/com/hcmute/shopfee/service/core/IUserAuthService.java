package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.ChangePasswordRequest;
import com.hcmute.shopfee.dto.request.RegisterUserRequest;
import com.hcmute.shopfee.dto.request.UpdatePasswordRequest;
import com.hcmute.shopfee.dto.response.LoginResponse;
import com.hcmute.shopfee.dto.response.RefreshTokenResponse;
import com.hcmute.shopfee.dto.response.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface IUserAuthService {
    RegisterResponse registerUser(RegisterUserRequest body);
    RegisterResponse firebaseRegisterUser(HttpServletRequest request);
    LoginResponse userLogin(String email, String password);
    LoginResponse firebaseUserLogin(HttpServletRequest request);
    void logoutUser(String refreshToken);
    void sendCodeToRegister(String email);
    void sendCodeToGetPassword(String email);
    void verifyCodeByEmail(String code, String email);
    void changePasswordForgot(ChangePasswordRequest body);
    RefreshTokenResponse refreshToken(String refreshToken);
    void changePasswordProfile(String userId, UpdatePasswordRequest data);
}
