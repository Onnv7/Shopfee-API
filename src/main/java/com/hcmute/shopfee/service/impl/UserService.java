package com.hcmute.shopfee.service.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.UpdateUserRequest;
import com.hcmute.shopfee.dto.response.GetAllUserResponse;
import com.hcmute.shopfee.dto.response.GetUserByIdResponse;
import com.hcmute.shopfee.entity.UserEntity;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.UserRepository;
import com.hcmute.shopfee.service.IUserService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final ModelMapperService modelMapperService;

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public GetAllUserResponse getUserList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<UserEntity> userPage = userRepository.findAll(pageable);
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
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + userId));
        return modelMapperService.mapClass(userEntity, GetUserByIdResponse.class);
    }

    @Override
    public void updateUserProfile(String userId, UpdateUserRequest body) {
        SecurityUtils.checkUserId(userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + userId));
        modelMapperService.map(body, user);
        userRepository.save(user);
    }

    @Override
    public String checkExistedUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + email));
        return user.getFullName();
    }
}
