package com.hcmute.shopfee.schedule.job;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.SystemNotificationEntity;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.repository.database.SystemNotificationRepository;
import com.hcmute.shopfee.service.common.FirebaseMessagingService;
import lombok.RequiredArgsConstructor;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@RequiredArgsConstructor
public class SendSystemNotificationJob extends QuartzJobBean {
    public static final String NOTIFICATION_ID = "notificationId";
    private final SystemNotificationRepository systemNotificationRepository;
    private final FirebaseMessagingService firebaseMessagingService;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String notificationId = jobDataMap.getString(NOTIFICATION_ID);
        SystemNotificationEntity notificationEntity = systemNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.NOTIFICATION_NOT_FOUND,  ErrorConstant.NOT_FOUND + notificationId));
        firebaseMessagingService.sendSystemNotification(notificationEntity);
    }
}
