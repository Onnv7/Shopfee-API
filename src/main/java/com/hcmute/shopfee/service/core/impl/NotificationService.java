package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.CreateEmployeeFcmTokenRequest;
import com.hcmute.shopfee.dto.request.CreateUserFcmTokenRequest;
import com.hcmute.shopfee.dto.request.UpdateFcmTokenRequest;
import com.hcmute.shopfee.dto.response.CreateFcmTokenResponse;
import com.hcmute.shopfee.entity.sql.database.EmployeeEntity;
import com.hcmute.shopfee.entity.sql.database.EmployeeFCMTokenEntity;
import com.hcmute.shopfee.entity.sql.database.UserFCMTokenEntity;
import com.hcmute.shopfee.entity.sql.database.UserEntity;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.EmployeeFCMTokenRepository;
import com.hcmute.shopfee.repository.database.EmployeeRepository;
import com.hcmute.shopfee.repository.database.UserFCMTokenRepository;
import com.hcmute.shopfee.repository.database.UserRepository;
import com.hcmute.shopfee.service.core.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {
    private final UserFCMTokenRepository userFcmTokenRepository;
    private final UserRepository userRepository;
    private final EmployeeFCMTokenRepository employeeFCMTokenRepository;
    private final EmployeeRepository employeeRepository;


    @Override
    public CreateFcmTokenResponse createUserFcmToken(CreateUserFcmTokenRequest body) {
        UserEntity user = null;
        if (body.getUserId() != null) {
            user = userRepository.findById(body.getUserId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.USER_ID_NOT_FOUND + body.getUserId()));
        }
        UserFCMTokenEntity fcmToken = new UserFCMTokenEntity();
        fcmToken.setUser(user);
        fcmToken.setToken(body.getToken());
        fcmToken = userFcmTokenRepository.save(fcmToken);
        CreateFcmTokenResponse data = new CreateFcmTokenResponse();
        data.setFcmTokenId(fcmToken.getId());
        return data;
    }

    @Override
    public CreateFcmTokenResponse createEmployeeFcmToken(CreateEmployeeFcmTokenRequest body) {
        EmployeeEntity user = null;
        if (body.getEmployeeId() != null) {
            user = employeeRepository.findById(body.getEmployeeId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.EMPLOYEE_ID_NOT_FOUND + body.getEmployeeId()));
        }
        EmployeeFCMTokenEntity fcmToken = new EmployeeFCMTokenEntity();
        fcmToken.setEmployee(user);
        fcmToken.setToken(body.getToken());
        fcmToken = employeeFCMTokenRepository.save(fcmToken);
        CreateFcmTokenResponse data = new CreateFcmTokenResponse();
        data.setFcmTokenId(fcmToken.getId());
        return data;
    }

    @Override
    public void updateUserFcmToken(UpdateFcmTokenRequest body) {

        UserFCMTokenEntity fcmToken = userFcmTokenRepository.findById(body.getFcmTokenId())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.FCM_TOKEN_ID_NOT_FOUND));
        UserEntity user = userRepository.findById(body.getUserId())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.USER_ID_NOT_FOUND));
        fcmToken.setUser(user);

        userFcmTokenRepository.save(fcmToken);
    }

}
