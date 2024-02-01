package com.hcmute.shopfee.service;

import com.hcmute.shopfee.dto.request.UpdateEmployeeRequest;
import com.hcmute.shopfee.dto.response.GetAllEmployeeResponse;
import com.hcmute.shopfee.enums.EmployeeStatus;

import java.util.List;

public interface IEmployeeService {
    GetAllEmployeeResponse getEmployeeList(String key, int page, int size, EmployeeStatus status);
    void updateEmployeeForAdmin(UpdateEmployeeRequest data, String id);
    void deleteEmployeeById(String id);
}
