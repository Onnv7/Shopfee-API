package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.database.RoleEntity;
import com.hcmute.shopfee.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, String> {
    Optional<RoleEntity> findByRoleName(Role role);
}
