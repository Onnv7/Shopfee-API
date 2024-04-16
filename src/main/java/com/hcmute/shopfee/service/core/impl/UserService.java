package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.CloudinaryConstant;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.common.CloudinaryUploadResponse;
import com.hcmute.shopfee.dto.request.AddPhoneNumberRequest;
import com.hcmute.shopfee.dto.request.UpdateUserRequest;
import com.hcmute.shopfee.dto.request.UploadUserAvatarRequest;
import com.hcmute.shopfee.dto.response.*;
import com.hcmute.shopfee.dto.sql.GetStatisticByKeyValue;
import com.hcmute.shopfee.dto.sql.GetUserSpendingStatisticDto;
import com.hcmute.shopfee.entity.sql.database.CoinHistoryEntity;
import com.hcmute.shopfee.entity.sql.database.UserEntity;
import com.hcmute.shopfee.enums.UserChartStatisticType;
import com.hcmute.shopfee.enums.UserStatus;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.CoinHistoryRepository;
import com.hcmute.shopfee.repository.database.payment.TransactionRepository;
import com.hcmute.shopfee.repository.database.UserRepository;
import com.hcmute.shopfee.repository.database.order.OrderEventRepository;
import com.hcmute.shopfee.service.common.CloudinaryService;
import com.hcmute.shopfee.service.core.IUserService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.utils.DateUtils;
import com.hcmute.shopfee.utils.MediaUtils;
import com.hcmute.shopfee.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final ModelMapperService modelMapperService;
    private final CloudinaryService cloudinaryService;
    private final TransactionRepository transactionRepository;
    private final OrderEventRepository orderEventRepository;
    private final CoinHistoryRepository coinHistoryRepository;

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
    public UploadAvatarResponse uploadAvatar(UploadUserAvatarRequest body, String userId) {
        UploadAvatarResponse response = new UploadAvatarResponse();
        if (!MediaUtils.isValidImageFile(body.getImage())) {
            throw new CustomException(ErrorConstant.IMAGE_INVALID);
        }
        try {
            SecurityUtils.checkUserId(userId);
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.USER_ID_NOT_FOUND + userId));

            byte[] imageBytes = body.getImage().getBytes();
            CloudinaryUploadResponse fileUploaded = cloudinaryService.uploadFileToFolder(CloudinaryConstant.USER_AVATAR_PATH, userId, imageBytes);

            user.setAvatarId(fileUploaded.getPublicId());
            user.setAvatarUrl(cloudinaryService.getThumbnailUrlOfImage(fileUploaded.getPublicId()));

            userRepository.save(user);
            response.setAvatarUrl(user.getAvatarUrl());
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addPhoneNumberToUser(AddPhoneNumberRequest body, String userId) {
        SecurityUtils.checkUserId(userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.USER_ID_NOT_FOUND + userId));

        if (user.getPhoneNumber() != null) {
            throw new CustomException(ErrorConstant.ACTING_INCORRECTLY, "The user already has a phone number");
        }
        user.setPhoneNumber(body.getPhoneNumber());
        userRepository.save(user);
    }

    @Override
    public GetUserSpendingStatisticsResponse getUserSpendingStatistic(String userId, Date startDate, Date endDate) {
        SecurityUtils.checkUserId(userId);
        if (!DateUtils.isWithin31Days(startDate, endDate)) {
            throw new CustomException(ErrorConstant.DATA_SEND_INVALID, "The selected time period exceeds 31 days");
        }
        List<GetUserSpendingStatisticDto> dataStatisticsList = transactionRepository.getUserSpendingStatisticsByDate(startDate, endDate, userId);
        return GetUserSpendingStatisticsResponse.fromDatabase(dataStatisticsList, startDate, endDate);
    }

    @Override
    public GetUserOrderStatusStatisticsResponse getOrderStatisticByUserId(String userId, UserChartStatisticType chartType) {
        SecurityUtils.checkUserId(userId);
        List<GetStatisticByKeyValue> dataList = new ArrayList<>();
        if (chartType == UserChartStatisticType.ORDER_STATUS) {
            dataList = orderEventRepository.getCountOrderEventStatisticsByUser(userId);
        } else if (chartType == UserChartStatisticType.PAYMENT_TYPE) {
            dataList = transactionRepository.getUserPaymentTypeStatistic(userId);
        }
        return GetUserOrderStatusStatisticsResponse.fromOrderStatusStatistics(dataList);
    }

    @Override
    public GetCoinHistoryListResponse getCoinHistoryList(String userId, int page, int size) {
        GetCoinHistoryListResponse data = new GetCoinHistoryListResponse();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CoinHistoryEntity> coinPage = coinHistoryRepository.findByUser_Id(userId, pageable);
        data.setTotalPage(coinPage.getTotalPages());
        data.setCoinHistoryList(coinPage.getContent());
        return data;
    }
}
