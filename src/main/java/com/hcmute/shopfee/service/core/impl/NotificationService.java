package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.UpsertEmployeeFcmTokenRequest;
import com.hcmute.shopfee.dto.request.UpsertUserFcmTokenRequest;
import com.hcmute.shopfee.dto.response.UpsertFcmTokenResponse;
import com.hcmute.shopfee.entity.sql.database.EmployeeEntity;
import com.hcmute.shopfee.entity.sql.database.EmployeeFCMTokenEntity;
import com.hcmute.shopfee.entity.sql.database.UserFCMTokenEntity;
import com.hcmute.shopfee.entity.sql.database.UserEntity;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
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
    public UpsertFcmTokenResponse upsertUserFcmToken(UpsertUserFcmTokenRequest body) {
        UserEntity user = null;
        if (body.getUserId() != null) {
            user = userRepository.findById(body.getUserId())
                    .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.USER_NOT_FOUND, ErrorConstant.NOT_FOUND + body.getUserId()));
        }
        UserFCMTokenEntity fcmTokenEntity = userFcmTokenRepository.findByToken(body.getToken())
                .orElse(null);
        if(fcmTokenEntity == null) {
            fcmTokenEntity = new UserFCMTokenEntity();
            fcmTokenEntity.setUser(user);
            fcmTokenEntity.setToken(body.getToken());
        } else {
            fcmTokenEntity.setUser(user);
        }

        fcmTokenEntity = userFcmTokenRepository.save(fcmTokenEntity);
        UpsertFcmTokenResponse data = new UpsertFcmTokenResponse();
        data.setFcmTokenId(fcmTokenEntity.getId());
        return data;
    }

    @Override
    public UpsertFcmTokenResponse upsertEmployeeFcmToken(UpsertEmployeeFcmTokenRequest body) {
        EmployeeEntity employee = null;
        if (body.getEmployeeId() != null) {
            employee = employeeRepository.findById(body.getEmployeeId())
                    .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.EMPLOYEE_NOT_FOUND, ErrorConstant.NOT_FOUND+ body.getEmployeeId()));
        }
        EmployeeFCMTokenEntity fcmTokenEntity = employeeFCMTokenRepository.findByToken(body.getToken())
                .orElse(null);
        if(fcmTokenEntity == null) {
            fcmTokenEntity = new EmployeeFCMTokenEntity();
            fcmTokenEntity.setEmployee(employee);
            fcmTokenEntity.setToken(body.getToken());
        } else {
            fcmTokenEntity.setEmployee(employee);
        }

        fcmTokenEntity = employeeFCMTokenRepository.save(fcmTokenEntity);
        UpsertFcmTokenResponse data = new UpsertFcmTokenResponse();
        data.setFcmTokenId(fcmTokenEntity.getId());
        return data;
    }
}
