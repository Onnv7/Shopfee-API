package com.hcmute.shopfee.command;


import com.hcmute.shopfee.entity.sql.database.EmployeeEntity;
import com.hcmute.shopfee.enums.EmployeeRole;
import com.hcmute.shopfee.enums.EmployeeStatus;
import com.hcmute.shopfee.enums.Gender;
import com.hcmute.shopfee.repository.database.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class CreateAdminCommand implements CommandLineRunner {
    private final EmployeeRepository employeeRepository;
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

        EmployeeEntity admin = EmployeeEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .firstName("An")
                .lastName("Nguyen")
                .status(EmployeeStatus.ACTIVE)
                .gender(Gender.MALE)
                .role(EmployeeRole.ROLE_ADMIN)
                .birthDate(Date.valueOf("2002-11-06"))
                .build();
        employeeRepository.save(admin);
        log.info("ADMIN IS CREATED");
    }
}
