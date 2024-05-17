package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.sql.database.SystemNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemNotificationRepository extends JpaRepository<SystemNotificationEntity, String> {
}
