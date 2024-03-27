package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.CreateEmployeeFcmTokenRequest;
import com.hcmute.shopfee.dto.request.CreateUserFcmTokenRequest;
import com.hcmute.shopfee.dto.request.UpdateFcmTokenRequest;
import com.hcmute.shopfee.dto.response.CreateFcmTokenResponse;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.core.INotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.hcmute.shopfee.constant.RouterConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Tag(name = NOTIFICATION_CONTROLLER_TITLE)
@RestController
@RequestMapping(NOTIFICATION_BASE_PATH)
@RequiredArgsConstructor
public class NotificationController {
    private final INotificationService notificationService;

    @Operation(summary = NOTIFICATION_CREATE_USER_TOKEN_SUM)
    @PostMapping(path = POST_NOTIFICATION_CREATE_USER_TOKEN_SUB_PATH)
    public ResponseEntity<ResponseAPI<CreateFcmTokenResponse>> createUserToken(@RequestBody @Valid CreateUserFcmTokenRequest body) {
        CreateFcmTokenResponse data = notificationService.createUserFcmToken(body);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(data)
                .message(SuccessConstant.UPDATED)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = NOTIFICATION_CREATE_EMPLOYEE_TOKEN_SUM)
    @PostMapping(path = POST_NOTIFICATION_CREATE_EMPLOYEE_TOKEN_SUB_PATH)
    public ResponseEntity<ResponseAPI<CreateFcmTokenResponse>> createEmployeeToken(@RequestBody @Valid CreateEmployeeFcmTokenRequest body) {
        CreateFcmTokenResponse data = notificationService.createEmployeeFcmToken(body);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(data)
                .message(SuccessConstant.UPDATED)
                .build();

        return new ResponseEntity<>(res, StatusCode.OK);
    }

//    @Operation(summary = NOTIFICATION_UPDATE_INFORMATION_SUM)
//    @PatchMapping(path = PATCH_NOTIFICATION_UPDATE_INFORMATION_SUB_PATH)
//    public ResponseEntity<ResponseAPI<?>> updateUserIdForFcm(@RequestBody @Valid UpdateFcmTokenRequest body) {
//        notificationService.updateUserFcmToken(body);
//        ResponseAPI<?> res = ResponseAPI.builder()
//                .timestamp(new Date())
//                .message(SuccessConstant.UPDATED)
//                .build();
//
//        return new ResponseEntity<>(res, StatusCode.OK);
//    }
}
