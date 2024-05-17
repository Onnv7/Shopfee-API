package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.SecurityConstant;
import com.hcmute.shopfee.constant.ShopfeeConstant;
import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.*;
import com.hcmute.shopfee.dto.response.EmployeeLoginResponse;
import com.hcmute.shopfee.dto.response.RefreshEmployeeTokenResponse;
import com.hcmute.shopfee.enums.Role;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.core.IEmployeeAuthService;
import com.hcmute.shopfee.utils.HeaderUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import static com.hcmute.shopfee.constant.RouterConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Tag(name = AUTH_CONTROLLER_TITLE)
@RestController
@RequiredArgsConstructor
@RequestMapping(EMPLOYEE_AUTH_BASE_PATH)
@Slf4j
public class EmployeeAuthController {
    private final IEmployeeAuthService employeeAuthService;

    @Operation(summary = AUTH_EMPLOYEE_LOGIN_SUM)
    @PostMapping(path = POST_EMPLOYEE_AUTH_LOGIN_SUB_PATH)
    public ResponseEntity<ResponseAPI<EmployeeLoginResponse>> loginEmployee(@RequestBody @Valid EmployeeLoginRequest body) throws ExecutionException, InterruptedException {
        EmployeeLoginResponse data = employeeAuthService.employeeLogin(body);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(data)
                .message(SuccessConstant.LOGIN)
                .build();
        HttpHeaders headers = HeaderUtils.setRefreshTokenCookie(data.getRefreshToken(), ShopfeeConstant.REFRESH_TOKEN_EXPIRE_MINUTES_TIME);

        return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }

    @Operation(summary = AUTH_EMPLOYEE_LOGOUT_SUM)
    @PostMapping(path = POST_EMPLOYEE_AUTH_LOGOUT_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN_MANAGER_WAITER)
    public ResponseEntity<ResponseAPI<?>> logoutEmployee(@RequestBody @Valid EmployeeLogoutRequest body,
                                                         @CookieValue(name = "refreshToken", required = true) String refreshToken) {

        employeeAuthService.employeeLogout(body, refreshToken);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.LOGOUT)
                .build();

        HttpHeaders headers = HeaderUtils.setRefreshTokenCookie("", 0);
        return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }

    @Operation(summary = AUTH_REFRESH_EMPLOYEE_TOKEN_SUM)
    @PostMapping(path = POST_EMPLOYEE_AUTH_REFRESH_TOKEN_SUB_PATH)
    public ResponseEntity<ResponseAPI<RefreshEmployeeTokenResponse>> refreshEmployeeToken(@CookieValue(name = "refreshToken", required = true) String refreshToken) {
        RefreshEmployeeTokenResponse data = employeeAuthService.refreshEmployeeToken(refreshToken);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(data)
                .message(SuccessConstant.GET_NEW_TOKEN)
                .build();


        HttpHeaders headers = HeaderUtils.setRefreshTokenCookie(data.getRefreshToken(), ShopfeeConstant.REFRESH_TOKEN_EXPIRE_MINUTES_TIME);
        return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }

    @Operation(summary = AUTH_EMPLOYEE_REGISTER_SUM)
    @PostMapping(path = POST_EMPLOYEE_AUTH_REGISTER_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN)
    public ResponseEntity<ResponseAPI<?>> registerEmployee(@RequestBody @Valid CreateEmployeeRequest body, @RequestParam("role") Role role) {
        employeeAuthService.employeeRegister(body, role);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Operation(summary = EMPLOYEE_UPDATE_PASSWORD_SUM)
    @PatchMapping(path = PATCH_EMPLOYEE_UPDATE_PASSWORD_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN_MANAGER_WAITER)
    public ResponseEntity<ResponseAPI<?>> changePasswordProfile(@PathVariable(EMPLOYEE_ID) String id, @RequestBody @Valid ChangePasswordEmployeeRequest body) {
        employeeAuthService.changePasswordProfile(body, id);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.UPDATED)
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = EMPLOYEE_SET_NEW_PASSWORD_SUM)
    @PatchMapping(path = PATCH_EMPLOYEE_SET_PASSWORD_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN_MANAGER)
    public ResponseEntity<ResponseAPI<?>> setPasswordByEmployeeId(@PathVariable(EMPLOYEE_ID) String id, @RequestBody @Valid SetPasswordByEmployeeIdRequest body) {
        employeeAuthService.setPasswordByEmployeeId(body, id);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.UPDATED)
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
