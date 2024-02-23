package com.hcmute.shopfee.command;

import com.hcmute.shopfee.entity.database.RoleEntity;
import com.hcmute.shopfee.enums.Role;
import com.hcmute.shopfee.repository.database.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class CreateRoleDataCommand implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        try {
            boolean existedAdmin = roleRepository.findByRoleName(Role.ROLE_ADMIN).orElse(null) != null;
            boolean existedUser = roleRepository.findByRoleName(Role.ROLE_USER).orElse(null) != null;
            boolean existedEmployee = roleRepository.findByRoleName(Role.ROLE_EMPLOYEE).orElse(null) != null;
            RoleEntity admin = RoleEntity.builder().roleName(Role.ROLE_ADMIN).build();
            RoleEntity user = RoleEntity.builder().roleName(Role.ROLE_USER).build();
            RoleEntity employee = RoleEntity.builder().roleName(Role.ROLE_EMPLOYEE).build();

            if(!existedAdmin) {
                roleRepository.save(admin);
                log.info("Admin role is created");
            } else {
                log.info("Admin role were existed");
            }

            if(!existedUser) {
                roleRepository.save(user);
                log.info("User role is created");
            } else {
                log.info("User role were existed");
            }

            if(!existedEmployee) {
                roleRepository.save(employee);
                log.info("Employee role is created");
            } else {
                log.info("Employee role were existed");
            }
        }
        catch (Exception e) {
            log.error("Create role command failed: {}", e.getMessage());
        }
    }
}
