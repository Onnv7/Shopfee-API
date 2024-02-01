package com.hcmute.shopfee.service.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.UpdateEmployeeRequest;
import com.hcmute.shopfee.dto.response.GetAllEmployeeResponse;
import com.hcmute.shopfee.entity.EmployeeEntity;
import com.hcmute.shopfee.enums.EmployeeStatus;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.EmployeeRepository;
import com.hcmute.shopfee.service.IEmployeeService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.utils.RegexUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapperService modelMapperService;

    public Optional<EmployeeEntity> findByUsername(String username) {
        return employeeRepository.findByUsernameAndIsDeletedFalse(username);
    }

    @Override
    public GetAllEmployeeResponse getEmployeeList(String key, int page, int size, EmployeeStatus status) {
        String statusRegex = RegexUtils.generateFilterRegexString(status != null ? status.name() : "");
        Pageable pageable = PageRequest.of(page - 1, size);
       GetAllEmployeeResponse response = new GetAllEmployeeResponse();
       List<GetAllEmployeeResponse.Employee> employeeListResponse = new ArrayList<>();
        Page<EmployeeEntity> employeePage = null;
        if (key == null) {
             employeePage = employeeRepository.getEmployeeList(statusRegex, pageable);
        } else {
            employeePage = employeeRepository.searchEmployee(key, statusRegex, pageable);
        }

        if(employeePage == null) {
            response.setTotalPage(0);
            response.setEmployeeList(new ArrayList<>());
            return response;
        }

        employeePage.getContent().forEach(it -> {
            GetAllEmployeeResponse.Employee employee = GetAllEmployeeResponse.Employee.fromEmployeeEntity(it);
            employeeListResponse.add(employee);
        });
        response.setEmployeeList(employeeListResponse);
        response.setTotalPage(employeePage.getTotalPages());
        return response;
    }

    @Override
    public void updateEmployeeForAdmin(UpdateEmployeeRequest data, String id) {
        EmployeeEntity employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + id));
        modelMapperService.mapNotNull(data, employee);
        employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployeeById(String id) {
        EmployeeEntity employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + id));
        employee.setDeleted(true);
        employeeRepository.save(employee);
    }


}
