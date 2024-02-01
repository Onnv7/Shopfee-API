package com.hcmute.shopfee.command;


import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.EmployeeEntity;
import com.hcmute.shopfee.entity.RoleEntity;
import com.hcmute.shopfee.enums.Gender;
import com.hcmute.shopfee.enums.Role;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.EmployeeRepository;
import com.hcmute.shopfee.repository.database.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class CreateAdminCommand implements CommandLineRunner {
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        EmployeeEntity existedAdmin = employeeRepository.findByUsernameAndIsDeletedFalse("admin").orElse(null);
        if(existedAdmin != null) {
            log.info("ADMIN IS EXISTED");
            return;
        }
        RoleEntity adminRole = roleRepository
                .findByRoleName(Role.ROLE_ADMIN)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + Role.ROLE_ADMIN));
        Set<RoleEntity> roleList = new HashSet<>(Collections.singleton(adminRole));

        EmployeeEntity admin = EmployeeEntity.builder()
                .username("admin")
                .password("$2a$07$8uAcnHtjJyuBjFq8c73jKuuKj/KbxqT79v7.fpVtzYUUUYvUmzbXG")
                .firstName("An")
                .lastName("Nguyen")
                .gender(Gender.MALE)
                .roleList(roleList)
                .birthDate(new Date(2002, 10, 6))
                .build();
        employeeRepository.save(admin);
        log.info("ADMIN IS CREATED");
    }
}
