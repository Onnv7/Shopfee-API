package com.hcmute.shopfee.service.core.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.UpsertNotificationFCMRequest;
import com.hcmute.shopfee.dto.request.UpsertEmployeeFcmTokenRequest;
import com.hcmute.shopfee.dto.request.UpsertUserFcmTokenRequest;
import com.hcmute.shopfee.dto.response.GetNotificationList;
import com.hcmute.shopfee.dto.response.GetSystemNotificationDetailResponse;
import com.hcmute.shopfee.dto.response.UpsertFcmTokenResponse;
import com.hcmute.shopfee.entity.sql.database.*;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.repository.database.*;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.service.common.SchedulerService;
import com.hcmute.shopfee.service.core.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.Get;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static com.hcmute.shopfee.constant.ShopfeeConstant.SYSTEM_FCM_TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {
    private final UserFCMTokenRepository userFcmTokenRepository;
    private final UserRepository userRepository;
    private final EmployeeFCMTokenRepository employeeFCMTokenRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapperService modelMapperService;
    private final SystemNotificationRepository systemNotificationRepository;


    @Override
    public UpsertFcmTokenResponse upsertUserFcmToken(UpsertUserFcmTokenRequest body)  {
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

       try {
           FirebaseMessaging.getInstance().subscribeToTopic(
                   Collections.singletonList(data.getFcmTokenId()),
                   SYSTEM_FCM_TOPIC
           );
       } catch (Exception e) {
           log.error(Arrays.toString(e.getStackTrace()));
       }
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
    private final SchedulerService schedulerService;

    @Override
    public void createNotification(UpsertNotificationFCMRequest body) {
        SystemNotificationEntity data = modelMapperService.mapClass(body, SystemNotificationEntity.class);
        if(body.getTriggerTime() == null) {
            data.setTriggerTime(new Date());
        }
        systemNotificationRepository.save(data);
        schedulerService.setAutoSendSystemNotification(data);
    }

    @Override
    public void updateNotification(String notificationId, UpsertNotificationFCMRequest body) {
        SystemNotificationEntity data = systemNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.NOTIFICATION_NOT_FOUND,  ErrorConstant.NOT_FOUND + notificationId));
        modelMapperService.map(body, data);

        if(body.getTriggerTime() == null) {
            data.setTriggerTime(new Date());
        }
        systemNotificationRepository.save(data);
        schedulerService.setAutoSendSystemNotification(data);
    }

    @Override
    public GetNotificationList getNotificationList(int page, int size) {
        GetNotificationList data = new GetNotificationList();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<SystemNotificationEntity> notificationEntityPage = systemNotificationRepository.findAll(pageable);
        data.setTotalPage(notificationEntityPage.getTotalPages());
        data.setNotificationList(GetNotificationList.fromSystemNotificationEntityList(notificationEntityPage.getContent()));
        return data;
    }

    @Override
    public GetSystemNotificationDetailResponse getNotificationDetailsById(String notificationId) {
        SystemNotificationEntity notificationEntity = systemNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.NOTIFICATION_NOT_FOUND));
        return modelMapperService.mapClass(notificationEntity, GetSystemNotificationDetailResponse.class);
    }
}
