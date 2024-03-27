package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.ChangePasswordEmployeeRequest;
import com.hcmute.shopfee.dto.request.CreateEmployeeRequest;
import com.hcmute.shopfee.dto.request.EmployeeLoginRequest;
import com.hcmute.shopfee.dto.request.EmployeeLogoutRequest;
import com.hcmute.shopfee.dto.response.EmployeeLoginResponse;
import com.hcmute.shopfee.dto.response.RefreshEmployeeTokenResponse;
import com.hcmute.shopfee.enums.Role;

import java.util.concurrent.ExecutionException;

public interface IEmployeeAuthService {
    EmployeeLoginResponse employeeLogin(EmployeeLoginRequest body) throws ExecutionException, InterruptedException;
    void employeeLogout(EmployeeLogoutRequest body, String refreshToken);
    RefreshEmployeeTokenResponse refreshEmployeeToken(String refreshToken);
    void employeeRegister(CreateEmployeeRequest body, Role role);
    void changePasswordProfile(ChangePasswordEmployeeRequest data, String emplId);
}
