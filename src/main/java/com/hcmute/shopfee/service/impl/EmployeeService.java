package com.hcmute.shopfee.service.impl;

import com.hcmute.shopfee.entity.EmployeeEntity;
import com.hcmute.shopfee.repository.database.EmployeeRepository;
import com.hcmute.shopfee.service.IEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {
    private final EmployeeRepository employeeRepository;

    public Optional<EmployeeEntity> findByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }
}
