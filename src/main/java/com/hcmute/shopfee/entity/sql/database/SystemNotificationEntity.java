package com.hcmute.shopfee.entity.sql.database;

import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Table(name = "system_notification")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SystemNotificationEntity {
    @Id
    @GenericGenerator(name = "system_notification_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "system_notification_id")
    private String id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "trigger_time")
    private Date triggerTime;
}
