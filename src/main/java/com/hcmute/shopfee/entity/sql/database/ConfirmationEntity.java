package com.hcmute.shopfee.entity.sql.database;

import com.hcmute.shopfee.enums.ConfirmationCodeStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;

@Entity
@Table(name = "confirmation")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ConfirmationEntity {
    @Id
    @GenericGenerator(name = "confirmation_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "confirmation_id")
    private String id;

    @Column(nullable = false)
    private String code;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private ConfirmationCodeStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expire_at")
    private Date expireAt;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;
}
