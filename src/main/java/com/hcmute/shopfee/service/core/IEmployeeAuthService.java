package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.dto.request.ChangePasswordEmployeeRequest;
import com.hcmute.shopfee.dto.request.CreateEmployeeRequest;
import com.hcmute.shopfee.dto.response.EmployeeLoginResponse;
import com.hcmute.shopfee.dto.response.RefreshEmployeeTokenResponse;
import com.hcmute.shopfee.enums.Role;

public interface IEmployeeAuthService {
    EmployeeLoginResponse attemptEmployeeLogin(String username, String password);
    void employeeLogout(String refreshToken);
    RefreshEmployeeTokenResponse refreshEmployeeToken(String refreshToken);
    void registerEmployee(CreateEmployeeRequest body, Role role);
    void changePasswordProfile(ChangePasswordEmployeeRequest data, String emplId);
}
