package com.hcmute.shopfee.controller;

import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.constant.SuccessConstant;
import com.hcmute.shopfee.dto.request.UpdateUserRequest;
import com.hcmute.shopfee.dto.request.UploadUserAvatarRequest;
import com.hcmute.shopfee.dto.response.GetAllUserResponse;
import com.hcmute.shopfee.dto.response.GetUserByIdResponse;
import com.hcmute.shopfee.model.ResponseAPI;
import com.hcmute.shopfee.service.core.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;

import static com.hcmute.shopfee.constant.RouterConstant.*;
import static com.hcmute.shopfee.constant.SwaggerConstant.*;

@Tag(name = USER_CONTROLLER_TITLE)
@RestController
@RequestMapping(USER_BASE_PATH)
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @Operation(summary = USER_GET_ALL_SUM)
    @GetMapping(path = GET_USER_ALL_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetAllUserResponse>> getUserList(
            @Parameter(name = "page", required = true, example = "1")
            @RequestParam("page") @Min(value = 1, message = "Page must be greater than 0") int page,
            @Parameter(name = "size", required = true, example = "10")
            @RequestParam("size") @Min(value = 1, message = "Size must be greater than 0") int size
    ) {
        GetAllUserResponse resData = userService.getUserList(page, size);

        ResponseAPI<GetAllUserResponse> res = ResponseAPI.<GetAllUserResponse>builder()
                .message(SuccessConstant.GET)
                .data(resData)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);

    }

    @Operation(summary = USER_GET_BY_ID_SUM)
    @GetMapping(path = GET_USER_BY_ID_SUB_PATH)
    public ResponseEntity<ResponseAPI<GetUserByIdResponse>> getUserProfileById(@PathVariable(USER_ID) String userId) {
        GetUserByIdResponse resData = userService.getUserProfileById(userId);

        ResponseAPI<GetUserByIdResponse> res = ResponseAPI.<GetUserByIdResponse>builder()
                .message(SuccessConstant.GET)
                .data(resData)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = USER_UPDATE_BY_ID_SUM)
    @PutMapping(path = PUT_USER_UPDATE_BY_ID_PATH)
    public ResponseEntity<ResponseAPI<?>> updateUserProfile(
            @PathVariable(USER_ID) String userId,
            @RequestBody @Valid UpdateUserRequest body
    ) {
        userService.updateUserProfile(userId, body);

        ResponseAPI<?> res = ResponseAPI.builder()
                .timestamp(new Date())
                .message(SuccessConstant.UPDATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = USER_CHECK_EXISTED_BY_EMAIL_SUM)
    @GetMapping(path = GET_USER_CHECK_EXISTED_SUB_PATH)
    public ResponseEntity<ResponseAPI<String>> checkExistedUserByEmail(@RequestParam("email") String email) {
        String result = userService.checkExistedUserByEmail(email);
        ResponseAPI<String> res = ResponseAPI.<String>builder()
                .timestamp(new Date())
                .message(SuccessConstant.GET)
                .data(result)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }

    @Operation(summary = USER_UPLOAD_AVATAR_BY_USER_ID_SUM)
    @PatchMapping(path = PATCH_USER_UPLOAD_AVATAR_SUB_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> uploadAvatar(@PathVariable(USER_ID) String userId, @ModelAttribute @Valid UploadUserAvatarRequest body) {
        userService.uploadAvatar(body, userId);
        ResponseAPI<String> res = ResponseAPI.<String>builder()
                .timestamp(new Date())
                .message(SuccessConstant.UPDATED)
                .build();
        return new ResponseEntity<>(res, StatusCode.OK);
    }
}
