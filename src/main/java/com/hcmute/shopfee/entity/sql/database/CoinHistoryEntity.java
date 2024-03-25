package com.hcmute.shopfee.entity.sql.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.enums.ActorType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

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
    @GenericGenerator(name = "coin_history_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "coin_history_id")
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
