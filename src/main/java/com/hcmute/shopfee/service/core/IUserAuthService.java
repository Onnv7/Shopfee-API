package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.payload.request.*;
import com.hcmute.shopfee.payload.response.LoginResponse;
import com.hcmute.shopfee.payload.response.RefreshTokenResponse;
import com.hcmute.shopfee.payload.response.RegisterResponse;

public interface IUserAuthService {
    RegisterResponse registerUser(RegisterUserRequest body);
    RegisterResponse firebaseRegisterUser(FirebaseRegisterRequest body, String idToken);
    LoginResponse userLogin(UserLoginRequest body);
    LoginResponse firebaseUserLogin(FirebaseLoginRequest body, String idToken);
    void logoutUser(UserLogoutRequest body, String refreshToken);
    void sendCodeToRegister(String email);
    void sendCodeToGetPassword(String email);
    void verifyCodeByEmail(String code, String email);
    void changePasswordForgot(ChangePasswordRequest body);
    RefreshTokenResponse refreshToken(String refreshToken);
    void changePasswordProfile(String userId, UpdatePasswordRequest data);
}
