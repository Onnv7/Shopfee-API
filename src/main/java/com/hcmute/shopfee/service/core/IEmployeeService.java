package com.hcmute.shopfee.service.core;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.hcmute.shopfee.payload.request.UpdateEmployeeProfileRequest;
import com.hcmute.shopfee.payload.request.UpdateEmployeeRequest;
import com.hcmute.shopfee.payload.response.GetAllEmployeeResponse;
import com.hcmute.shopfee.payload.response.GetEmployeeByIdResponse;
import com.hcmute.shopfee.payload.response.GetEmployeeProfileByIdResponse;
import com.hcmute.shopfee.payload.response.GetSaleStatisticTodayResponse;
import com.hcmute.shopfee.enums.EmployeeStatus;

import java.sql.Date;
import java.util.concurrent.ExecutionException;

public interface IEmployeeService {
    GetAllEmployeeResponse getEmployeeList(String key, int page, int size, EmployeeStatus status);
    GetAllEmployeeResponse getEmployeeListByBranchId(String branchId, String key, int page, int size, EmployeeStatus status);
    void updateEmployee(UpdateEmployeeRequest data, String id) throws ExecutionException, InterruptedException, FirebaseMessagingException;
    void updateEmployeeProfile(UpdateEmployeeProfileRequest data, String id);
    void deleteEmployeeById(String employeeId);
    GetEmployeeProfileByIdResponse getEmployeeProfileById(String employeeId);
    GetEmployeeByIdResponse getEmployeeById(String employeeId);
    GetSaleStatisticTodayResponse getStatisticToday(String employeeId, Date startDate, Date endDate);
}
