package com.hcmute.shopfee.payload.response;

import com.hcmute.shopfee.entity.sql.database.SystemNotificationEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class GetNotificationList {
    private Integer totalPage;
    private List<Notification> notificationList;
    @Data
    private static class Notification {
        private String id;
        private String title;
        private Date triggerTime;
        private static Notification fromSystemNotificationEntity(SystemNotificationEntity entity) {
            Notification data = new Notification();
            data.setId(entity.getId());
            data.setTitle(entity.getTitle());
            data.setTriggerTime(entity.getTriggerTime());
            return data;
        }
    }
    public static List<Notification> fromSystemNotificationEntityList(List<SystemNotificationEntity> entityList) {
        List<Notification> data = new ArrayList<>();
        for(SystemNotificationEntity entity : entityList) {
            data.add(Notification.fromSystemNotificationEntity(entity));
        }
        return data;
    }
}
