package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.*;
import com.hcmute.shopfee.dto.response.LoginResponse;
import com.hcmute.shopfee.dto.response.RefreshTokenResponse;
import com.hcmute.shopfee.dto.response.RegisterResponse;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.core.IUserAuthService;
import com.hcmute.shopfee.utils.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.hcmute.shopfee.constant.RouterConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Tag(name = USER_AUTH_CONTROLLER_TITLE)
@RestController
@RequiredArgsConstructor
@RequestMapping(USER_AUTH_BASE_PATH)
@Slf4j
public class UserAuthController {
    private final IUserAuthService userAuthService;

    @Operation(summary = USER_AUTH_REGISTER_SUM)
    @PostMapping(POST_USER_AUTH_REGISTER_SUB_PATH)
    public ResponseEntity<ResponseAPI<RegisterResponse>> registerUser(@RequestBody @Valid RegisterUserRequest body) {
        RegisterResponse resDate = userAuthService.registerUser(body);

        ResponseAPI<RegisterResponse> res = ResponseAPI.<RegisterResponse>builder()
                .timestamp(new Date())
                .data(resDate)
                .message(SuccessConstant.CREATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = USER_AUTH_LOGIN_SUM)
    @PostMapping(POST_USER_AUTH_LOGIN_SUB_PATH)
    public ResponseEntity<ResponseAPI<LoginResponse>> loginUser(@RequestBody @Valid LoginRequest body) {
        LoginResponse data = userAuthService.userLogin(body.getEmail(), body.getPassword());
        ResponseAPI<LoginResponse> res = ResponseAPI.<LoginResponse>builder()
                .timestamp(new Date())
                .success(true)
                .message(SuccessConstant.LOGIN)
                .data(data)
                .build();

        HttpHeaders headers = new HttpHeaders();
        // TODO: kiem tra expire coookie
        headers.add(HttpHeaders.SET_COOKIE, "refreshToken=" + data.getRefreshToken() + "; Max-Age=604800; Path=/; Secure; HttpOnly");
        return new ResponseEntity<>(res, headers, StatusCode.OK);

    }

    @Operation(summary = USER_AUTH_LOGOUT_SUM)
    @GetMapping(path = GET_AUTH_USER_LOGOUT_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> logoutUser(HttpServletRequest request) {

        String refreshToken = CookieUtils.getRefreshToken(request);
        if (refreshToken == null) {
            throw new CustomException(ErrorConstant.NOT_FOUND + "refresh token");
        }
        userAuthService.logoutUser(refreshToken);

        ResponseAPI<?> res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.LOGOUT)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, "refreshToken=" + "; Max-Age=0; Path=/; Secure; HttpOnly");
        return new ResponseEntity<>(res, headers, StatusCode.OK);
    }

    @Operation(summary = USER_AUTH_RE_SEND_EMAIL_SUM)
    @PostMapping(POST_USER_AUTH_RE_SEND_EMAIL_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> resendEmail(@RequestBody @Valid ResendEmailRequest body) {
        userAuthService.resendCode(body.getEmail());
        ResponseAPI<?> res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.SEND_CODE_TO_EMAIL)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = USER_AUTH_SEND_CODE_TO_EMAIL_TO_REGISTER_SUM)
    @PostMapping(POST_AUTH_SEND_CODE_TO_REGISTER_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> sendCodeToRegister(@RequestBody @Valid SendCodeRequest body) {

        userAuthService.sendCodeToRegister(body.getEmail());
        ResponseAPI<?> res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.SEND_CODE_TO_EMAIL)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);

    }

    @Operation(summary = USER_AUTH_SEND_CODE_TO_EMAIL_TO_GET_PWD_SUM)
    @PostMapping(POST_USER_AUTH_SEND_CODE_TO_GET_PWD_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> sendCodeToGetPassword(@RequestBody @Valid SendCodeRequest body) {

        userAuthService.sendCodeToGetPassword(body.getEmail());
        ResponseAPI<?> res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.SEND_CODE_TO_EMAIL)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);

    }

    @Operation(summary = USER_AUTH_VERIFY_EMAIL_SUM)
    @PostMapping(POST_USER_AUTH_VERIFY_EMAIL_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> verifyCodeByEmail(@RequestBody @Valid VerifyEmailRequest body) {

        log.debug("YOUR CODE: " + body.getCode());
        userAuthService.verifyCodeByEmail(body.getCode(), body.getEmail());
        ResponseAPI<?> res = ResponseAPI.builder()
                .message(SuccessConstant.EMAIL_VERIFIED)
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);

    }

    @Operation(summary = USER_AUTH_CHANGE_PASSWORD_SUM)
    @PatchMapping(PATCH_USER_AUTH_CHANGE_PASSWORD_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> changePasswordForgot(@RequestBody @Valid ChangePasswordRequest body) {
        try {
            userAuthService.changePasswordForgot(body.getEmail(), body.getPassword());

            ResponseAPI<?> res = ResponseAPI.builder()
                    .message(SuccessConstant.UPDATED)
                    .timestamp(new Date())
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = USER_AUTH_REFRESH_TOKEN_SUM)
    @PostMapping(path = POST_USER_AUTH_REFRESH_TOKEN_SUB_PATH)
    public ResponseEntity<ResponseAPI<RefreshTokenResponse>> refreshToken(@RequestBody @Valid RefreshTokenRequest body) {
        RefreshTokenResponse data = userAuthService.refreshToken(body.getRefreshToken());

        ResponseAPI<RefreshTokenResponse> res = ResponseAPI.<RefreshTokenResponse>builder()
                .timestamp(new Date())
                .data(data)
                .message(SuccessConstant.GET)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = USER_CHANGE_PWD_SUM)
    @PatchMapping(path = PATCH_USER_CHANGE_PASSWORD_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> changePasswordProfile(
            @PathVariable(USER_ID) String userId,
            @RequestBody @Valid UpdatePasswordRequest body
    ) {
        userAuthService.changePasswordProfile(userId, body);
        ResponseAPI<?> res = ResponseAPI.builder()
                .timestamp(new Date())
                .success(true)
                .message(SuccessConstant.UPDATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }
}
