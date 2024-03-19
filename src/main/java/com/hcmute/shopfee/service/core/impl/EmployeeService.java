package com.hcmute.shopfee.service.core.impl;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.UpdateEmployeeProfileRequest;
import com.hcmute.shopfee.dto.request.UpdateEmployeeRequest;
import com.hcmute.shopfee.dto.response.GetAllEmployeeResponse;
import com.hcmute.shopfee.dto.response.GetEmployeeByIdResponse;
import com.hcmute.shopfee.dto.response.GetEmployeeProfileByIdResponse;
import com.hcmute.shopfee.entity.database.EmployeeEntity;
import com.hcmute.shopfee.enums.EmployeeStatus;
import com.hcmute.shopfee.enums.Role;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.repository.database.EmployeeRepository;
import com.hcmute.shopfee.service.core.IEmployeeService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.utils.RegexUtils;
import com.hcmute.shopfee.utils.SecurityUtils;
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

        if (employeePage == null) {
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
    public GetAllEmployeeResponse getEmployeeListByBranchId(String branchId, String key, int page, int size, EmployeeStatus status) {
        String statusRegex = RegexUtils.generateFilterRegexString(status != null ? status.name() : "");
        Pageable pageable = PageRequest.of(page - 1, size);
        GetAllEmployeeResponse response = new GetAllEmployeeResponse();
        List<GetAllEmployeeResponse.Employee> employeeListResponse = new ArrayList<>();
        Page<EmployeeEntity> employeePage = null;
        if (key == null) {
            employeePage = employeeRepository.getEmployeeListByBranchId(branchId, statusRegex, pageable);
        } else {
            employeePage = employeeRepository.searchEmployeeByBranchId(branchId, key, statusRegex, pageable);
        }

        if (employeePage == null) {
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
        List<String> roleList = SecurityUtils.getRoleList();

        EmployeeEntity employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.EMPLOYEE_ID_NOT_FOUND + id));

        if (SecurityUtils.isOnlyRole(roleList, Role.ROLE_MANAGER)) {
            EmployeeEntity manager = employeeRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.EMPLOYEE_ID_NOT_FOUND + SecurityUtils.getCurrentUserId()));
            if (!manager.getBranch().getId().equals(employee.getBranch().getId())) {
                throw new CustomException(ErrorConstant.FORBIDDEN, "Manager cannot update an employee account belonging to another branch");
            }
        }

        modelMapperService.mapNotNull(data, employee);
        employeeRepository.save(employee);
    }

    @Override
    public void updateEmployeeProfile(UpdateEmployeeProfileRequest data, String id) {
        SecurityUtils.checkUserId(id);
        EmployeeEntity employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.EMPLOYEE_ID_NOT_FOUND + id));
        modelMapperService.mapNotNull(data, employee);
        employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployeeById(String id) {
        List<String> roleList = SecurityUtils.getRoleList();

        EmployeeEntity employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.EMPLOYEE_ID_NOT_FOUND + id));

        if (SecurityUtils.isOnlyRole(roleList, Role.ROLE_MANAGER)) {
            EmployeeEntity manager = employeeRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.EMPLOYEE_ID_NOT_FOUND + SecurityUtils.getCurrentUserId()));
            if (!manager.getBranch().getId().equals(employee.getBranch().getId())) {
                throw new CustomException(ErrorConstant.FORBIDDEN, "Manager cannot delete an employee account belonging to another branch");
            }
        }
        employee.setDeleted(true);
        employeeRepository.save(employee);
    }

    @Override
    public GetEmployeeProfileByIdResponse getEmployeeProfileById(String employeeId) {
        SecurityUtils.checkUserId(employeeId);
        EmployeeEntity employee = employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.EMPLOYEE_ID_NOT_FOUND + employeeId));
        return modelMapperService.mapClass(employee, GetEmployeeProfileByIdResponse.class);
    }

    @Override
    public GetEmployeeByIdResponse getEmployeeById(String employeeId) {
        EmployeeEntity employee = employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.EMPLOYEE_ID_NOT_FOUND + employeeId));
        return GetEmployeeByIdResponse.fromEmployeeEntity(employee);
    }


}
