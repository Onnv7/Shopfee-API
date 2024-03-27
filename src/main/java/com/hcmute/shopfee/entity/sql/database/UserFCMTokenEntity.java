package com.hcmute.shopfee.entity.sql.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;

@Entity
@Table(name = "fcm_token_user")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFCMTokenEntity {
    @Id
    @GenericGenerator(name = "fcm_token_user_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "fcm_token_user_id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private UserEntity user;

    @Column(name = "token", nullable = false, unique = true)
    private String token;
}
