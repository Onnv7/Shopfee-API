package com.hcmute.shopfee.service;

import com.hcmute.shopfee.dto.request.ChangePasswordEmployeeRequest;
import com.hcmute.shopfee.dto.request.CreateEmployeeRequest;
import com.hcmute.shopfee.dto.response.EmployeeLoginResponse;
import com.hcmute.shopfee.dto.response.RefreshEmployeeTokenResponse;

public interface IEmployeeAuthService {
    EmployeeLoginResponse attemptEmployeeLogin(String username, String password);
    RefreshEmployeeTokenResponse refreshEmployeeToken(String refreshToken);
    void registerEmployee(CreateEmployeeRequest body);
    void changePasswordProfile(ChangePasswordEmployeeRequest data, String emplId);
}
