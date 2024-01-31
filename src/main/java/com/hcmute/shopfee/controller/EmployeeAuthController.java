package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.EmployeeLoginRequest;
import com.hcmute.shopfee.dto.request.RefreshEmployeeTokenRequest;
import com.hcmute.shopfee.dto.response.EmployeeLoginResponse;
import com.hcmute.shopfee.dto.response.RefreshEmployeeTokenResponse;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.IEmployeeAuthService;
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
    public ResponseEntity<ResponseAPI> loginEmployee(@RequestBody @Valid EmployeeLoginRequest body) {
        EmployeeLoginResponse data = employeeAuthService.attemptEmployeeLogin(body.getUsername(), body.getPassword());

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(data)
                .message(SuccessConstant.LOGIN)
                .build();
        HttpHeaders headers = new HttpHeaders();

        // TODO: kiem tra expire coookie
        headers.add(HttpHeaders.SET_COOKIE, "refreshToken=" + data.getRefreshToken() + "; Max-Age=604800; Path=/; Secure; HttpOnly");


        return new ResponseEntity<>(res, headers, StatusCode.OK);

    }
    @Operation(summary = AUTH_REFRESH_EMPLOYEE_TOKEN_SUM)
    @PostMapping(path = POST_AUTH_REFRESH_EMPLOYEE_TOKEN_SUB_PATH)
    public ResponseEntity<ResponseAPI> refreshEmployeeToken(
            @RequestBody(required = false) RefreshEmployeeTokenRequest body, HttpServletRequest request
            , @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        System.out.println(request);
        if (refreshToken == null &&  body == null) {
            throw new CustomException(ErrorConstant.INVALID_TOKEN);
        }
        RefreshEmployeeTokenResponse data = employeeAuthService.refreshEmployeeToken( body == null ? refreshToken : body.getRefreshToken());

        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(data)
                .message(SuccessConstant.GET_NEW_TOKEN)
                .build();

        HttpHeaders headers = new HttpHeaders();


        // TODO: kiem tra expire coookie
        headers.add(HttpHeaders.SET_COOKIE, "refreshToken=" + data.getRefreshToken() + "; Max-Age=604800; Path=/; Secure; HttpOnly");

        return new ResponseEntity<>(res, headers, StatusCode.OK);
    }
}
