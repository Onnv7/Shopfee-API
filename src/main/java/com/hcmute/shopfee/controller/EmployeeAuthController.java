package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.ChangePasswordEmployeeRequest;
import com.hcmute.shopfee.dto.request.CreateEmployeeRequest;
import com.hcmute.shopfee.dto.request.EmployeeLoginRequest;
import com.hcmute.shopfee.dto.request.RefreshEmployeeTokenRequest;
import com.hcmute.shopfee.dto.response.EmployeeLoginResponse;
import com.hcmute.shopfee.dto.response.RefreshEmployeeTokenResponse;
import com.hcmute.shopfee.enums.Role;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.core.IEmployeeAuthService;
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

@Tag(name = AUTH_CONTROLLER_TITLE)
@RestController
@RequiredArgsConstructor
@RequestMapping(EMPLOYEE_AUTH_BASE_PATH)
@Slf4j
public class EmployeeAuthController {
    private final IEmployeeAuthService employeeAuthService;

    @Operation(summary = AUTH_EMPLOYEE_LOGIN_SUM)
    @PostMapping(path = POST_AUTH_EMPLOYEE_LOGIN_SUB_PATH)
    public ResponseEntity<ResponseAPI<EmployeeLoginResponse>> loginEmployee(@RequestBody @Valid EmployeeLoginRequest body) {
        EmployeeLoginResponse data = employeeAuthService.attemptEmployeeLogin(body.getUsername(), body.getPassword());

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(data)
                .message(SuccessConstant.LOGIN)
                .build();
        HttpHeaders headers = CookieUtils.setRefreshTokenCookie(data.getRefreshToken(), 604800L);

        return new ResponseEntity<>(res, headers, StatusCode.OK);
    }

    @Operation(summary = AUTH_EMPLOYEE_LOGOUT_SUM)
    @GetMapping(path = GET_AUTH_EMPLOYEE_LOGOUT_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> logoutEmployee(HttpServletRequest request) {

        String refreshToken = CookieUtils.getRefreshToken(request);
        if(refreshToken == null) {
            throw new CustomException(ErrorConstant.NOT_FOUND, "Token is null");
        }
        employeeAuthService.employeeLogout(refreshToken);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.LOGOUT)
                .build();

        HttpHeaders headers = CookieUtils.setRefreshTokenCookie("", 0L);
        return new ResponseEntity<>(res, headers, StatusCode.OK);
    }

    @Operation(summary = AUTH_REFRESH_EMPLOYEE_TOKEN_SUM)
    @PostMapping(path = POST_AUTH_REFRESH_EMPLOYEE_TOKEN_SUB_PATH)
    public ResponseEntity<ResponseAPI<RefreshEmployeeTokenResponse>> refreshEmployeeToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null) {
            throw new CustomException(ErrorConstant.UNAUTHORIZED, "Token is null");
        }
        RefreshEmployeeTokenResponse data = employeeAuthService.refreshEmployeeToken(refreshToken);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(data)
                .message(SuccessConstant.GET_NEW_TOKEN)
                .build();


        HttpHeaders headers = CookieUtils.setRefreshTokenCookie(data.getRefreshToken(), 604800L);
        return new ResponseEntity<>(res, headers, StatusCode.OK);
    }

    @Operation(summary = AUTH_EMPLOYEE_REGISTER_SUM)
    @PostMapping(path = POST_AUTH_EMPLOYEE_REGISTER_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> registerEmployee(@RequestBody @Valid CreateEmployeeRequest body, @RequestParam("role") Role role) {
        employeeAuthService.registerEmployee(body, role);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();

        return new ResponseEntity<>(res, StatusCode.CREATED);
    }

    @Operation(summary = EMPLOYEE_UPDATE_PASSWORD_SUM)
    @PatchMapping(path = PATCH_EMPLOYEE_UPDATE_PASSWORD_SUB_PATH)
    public ResponseEntity<ResponseAPI<?>> changePasswordProfile(@PathVariable(EMPLOYEE_ID) String id, @RequestBody @Valid ChangePasswordEmployeeRequest body) {
        employeeAuthService.changePasswordProfile(body, id);

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.UPDATED)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }
}
