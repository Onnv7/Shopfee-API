package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.UpdateEmployeeProfileRequest;
import com.hcmute.shopfee.dto.request.UpdateEmployeeRequest;
import com.hcmute.shopfee.dto.response.GetAllEmployeeResponse;
import com.hcmute.shopfee.dto.response.GetEmployeeByIdResponse;
import com.hcmute.shopfee.dto.response.GetEmployeeProfileByIdResponse;
import com.hcmute.shopfee.enums.EmployeeStatus;

import java.util.List;

public interface IEmployeeService {
    GetAllEmployeeResponse getEmployeeList(String key, int page, int size, EmployeeStatus status);
    GetAllEmployeeResponse getEmployeeListByBranchId(String branchId, String key, int page, int size, EmployeeStatus status);
    void updateEmployeeForAdmin(UpdateEmployeeRequest data, String id);
    void updateEmployeeProfile(UpdateEmployeeProfileRequest data, String id);
    void deleteEmployeeById(String id);
    GetEmployeeProfileByIdResponse getEmployeeProfileById(String employeeId);
    GetEmployeeByIdResponse getEmployeeById(String employeeId);
}
