package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.SecurityConstant;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.UpsertNotificationFCMRequest;
import com.hcmute.shopfee.dto.request.UpsertEmployeeFcmTokenRequest;
import com.hcmute.shopfee.dto.request.UpsertUserFcmTokenRequest;
import com.hcmute.shopfee.dto.response.GetNotificationList;
import com.hcmute.shopfee.dto.response.GetSystemNotificationDetailResponse;
import com.hcmute.shopfee.dto.response.UpsertFcmTokenResponse;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.core.INotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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


    @Operation(summary = NOTIFICATION_GET_DETAIL_SUM)
    @GetMapping(path = GET_NOTIFICATION_DETAIL_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN)
    public ResponseEntity<ResponseAPI<GetSystemNotificationDetailResponse>> getNotificationDetailsById(
            @PathVariable(NOTIFICATION_ID) String notificationId
    ) {
        GetSystemNotificationDetailResponse data = notificationService.getNotificationDetailsById(notificationId);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(data)
                .message(SuccessConstant.GET)
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = NOTIFICATION_GET_LIST_SUM)
    @GetMapping(path = GET_NOTIFICATION_LIST_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN)
    public ResponseEntity<ResponseAPI<GetNotificationList>> getNotificationList(
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size
    ) {
        GetNotificationList data = notificationService.getNotificationList(page, size);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(data)
                .message(SuccessConstant.GET)
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    @Operation(summary = NOTIFICATION_UPDATE_SUM)
    @PutMapping(path = PUT_NOTIFICATION_UPDATE_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN)
    public ResponseEntity<ResponseAPI<?>> updateNotification(@PathVariable(NOTIFICATION_ID) String notificationId, @RequestBody @Valid UpsertNotificationFCMRequest body) {
        notificationService.updateNotification(notificationId, body);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.UPDATED)
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = NOTIFICATION_CREATE_SUM)
    @PostMapping(path = POST_NOTIFICATION_CREATE_SUB_PATH)
    @PreAuthorize(SecurityConstant.ROLE_ADMIN)
    public ResponseEntity<ResponseAPI<?>> createNotification(@RequestBody @Valid UpsertNotificationFCMRequest body) {
        notificationService.createNotification(body);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.CREATED)
                .build();

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Operation(summary = NOTIFICATION_CREATE_USER_TOKEN_SUM)
    @PostMapping(path = POST_NOTIFICATION_CREATE_USER_TOKEN_SUB_PATH)
    public ResponseEntity<ResponseAPI<UpsertFcmTokenResponse>> upsertUserFcmToken(@RequestBody @Valid UpsertUserFcmTokenRequest body) {
        UpsertFcmTokenResponse data = notificationService.upsertUserFcmToken(body);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(data)
                .message(SuccessConstant.UPDATED)
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = NOTIFICATION_CREATE_EMPLOYEE_TOKEN_SUM)
    @PostMapping(path = POST_NOTIFICATION_CREATE_EMPLOYEE_TOKEN_SUB_PATH)
    public ResponseEntity<ResponseAPI<UpsertFcmTokenResponse>> createEmployeeToken(@RequestBody @Valid UpsertEmployeeFcmTokenRequest body) {
        UpsertFcmTokenResponse data = notificationService.upsertEmployeeFcmToken(body);
        ResponseAPI res = ResponseAPI.builder()
                .timestamp(new Date())
                .data(data)
                .message(SuccessConstant.UPDATED)
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
