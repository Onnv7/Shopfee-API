package com.hcmute.shopfee.command;


import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.EmployeeEntity;
import com.hcmute.shopfee.entity.sql.database.RoleEntity;
import com.hcmute.shopfee.enums.EmployeeStatus;
import com.hcmute.shopfee.enums.Gender;
import com.hcmute.shopfee.enums.Role;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.repository.database.EmployeeRepository;
import com.hcmute.shopfee.repository.database.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class CreateAdminCommand implements CommandLineRunner {
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${app.username_admin}")
    private String username;

    @Value("${app.password_admin}")
    private String password;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        EmployeeEntity existedAdmin = employeeRepository.findByUsernameAndIsDeletedFalse(username).orElse(null);
        if (existedAdmin != null) {
            log.info("ADMIN IS EXISTED");
            return;
        }
        RoleEntity adminRole = roleRepository
                .findByRoleName(Role.ROLE_ADMIN)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.ROLE_NOT_FOUND, ErrorConstant.NOT_FOUND_WITH_INPUT + Role.ROLE_ADMIN.name()));
        Set<RoleEntity> roleList = new HashSet<>(Collections.singleton(adminRole));

        EmployeeEntity admin = EmployeeEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .firstName("An")
                .lastName("Nguyen")
                .status(EmployeeStatus.ACTIVE)
                .gender(Gender.MALE)
                .roleList(roleList)
                .birthDate(Date.valueOf("2002-11-06"))
                .build();
        employeeRepository.save(admin);
        log.info("ADMIN IS CREATED");
    }
}
