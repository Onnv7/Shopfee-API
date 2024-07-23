package com.hcmute.shopfee.entity.sql.database.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.user.UserEntity;
import com.hcmute.shopfee.enums.ActorType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;

@Entity
@Table(name = "coin_history")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CoinHistoryEntity {
    @Id
    @GenericGenerator(name = "coin_history_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "coin_history_id")
    @Column(length = 16)
    private String id;

    @Column(name = "coin", nullable = false)
    private Long coin;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor", nullable = false)
    private ActorType actor;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private UserEntity user;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;


}
