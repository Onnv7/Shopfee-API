package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.CloudinaryConstant;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.UpdateUserRequest;
import com.hcmute.shopfee.dto.request.UploadUserAvatarRequest;
import com.hcmute.shopfee.dto.response.GetAllUserResponse;
import com.hcmute.shopfee.dto.response.GetUserByIdResponse;
import com.hcmute.shopfee.entity.sql.database.UserEntity;
import com.hcmute.shopfee.enums.UserStatus;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.UserRepository;
import com.hcmute.shopfee.service.common.CloudinaryService;
import com.hcmute.shopfee.service.core.IUserService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.utils.ImageUtils;
import com.hcmute.shopfee.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final ModelMapperService modelMapperService;
    private final CloudinaryService cloudinaryService;

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public GetAllUserResponse getUserList(String key, UserStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<UserEntity> userPage = userRepository.getUserWithFilterAndKey(key == null ? "" : key, status == null ? "" : status.name(), pageable);
        GetAllUserResponse response = new GetAllUserResponse();
        List<GetAllUserResponse.UserInfo> userList = new ArrayList<>();
        userPage.getContent().forEach(it -> {
            GetAllUserResponse.UserInfo user = GetAllUserResponse.UserInfo.fromUserEntity(it);
            userList.add(user);
        });
        response.setTotalPage(userPage.getTotalPages());
        response.setUserList(userList);
        return response;
    }

    @Override
    public GetUserByIdResponse getUserProfileById(String userId) {
        SecurityUtils.checkUserId(userId);
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.USER_ID_NOT_FOUND + userId));
        return modelMapperService.mapClass(userEntity, GetUserByIdResponse.class);
    }

    @Override
    public void updateUserProfile(String userId, UpdateUserRequest body) {
        SecurityUtils.checkUserId(userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.USER_ID_NOT_FOUND + userId));
        modelMapperService.map(body, user);
        userRepository.save(user);
    }

    @Override
    public String checkExistedUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.USER_EMAIL_NOT_FOUND + email));
        return user.getFullName();
    }

    @Override
    public void uploadAvatar(UploadUserAvatarRequest body, String userId) {
        if (!ImageUtils.isValidImageFile(body.getImage())) {
            throw new CustomException(ErrorConstant.IMAGE_INVALID);
        }
        try {
            SecurityUtils.checkUserId(userId);
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.USER_ID_NOT_FOUND + userId));

            byte[] imageBytes = body.getImage().getBytes();
            HashMap<String, String> fileUploaded = cloudinaryService.uploadFileToFolder(CloudinaryConstant.USER_AVATAR_PATH, userId, imageBytes);
            fileUploaded.get(CloudinaryConstant.URL_PROPERTY);

            user.setAvatarId(fileUploaded.get(CloudinaryConstant.PUBLIC_ID));
            user.setAvatarUrl(cloudinaryService.getThumbnailUrl(fileUploaded.get(CloudinaryConstant.PUBLIC_ID)));

            userRepository.save(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
