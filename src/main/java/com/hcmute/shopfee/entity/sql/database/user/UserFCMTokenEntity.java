package com.hcmute.shopfee.entity.sql.database.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hcmute.shopfee.entity.sql.database.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import com.hcmute.shopfee.entity.sql.database.identifier.RandomTimeGenerator;

@Entity
@Table(name = "fcm_token_user")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFCMTokenEntity {
    @Id
    @GenericGenerator(name = "fcm_token_user_id", type = RandomTimeGenerator.class)
    @GeneratedValue(generator = "fcm_token_user_id")
    @Column(length = 16)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private UserEntity user;

    @Column(name = "token", nullable = false, unique = true)
    private String token;
}
