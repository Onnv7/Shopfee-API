package com.hcmute.shopfee.entity.sql.database;

import com.hcmute.shopfee.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;

import static com.hcmute.shopfee.constant.EntityConstant.TIME_ID_GENERATOR;


@Entity
@Table(name = "role")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {

    @Id
    @GenericGenerator(name = "role_id", strategy = TIME_ID_GENERATOR)
    @GeneratedValue(generator = "role_id")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, unique = true)
    private Role roleName;

    @ManyToMany(mappedBy = "roleList")
    private Set<UserEntity> userList;
}
