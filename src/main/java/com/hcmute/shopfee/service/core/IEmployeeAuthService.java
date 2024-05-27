package com.hcmute.shopfee.service.core;

import com.hcmute.shopfee.payload.request.*;
import com.hcmute.shopfee.payload.response.EmployeeLoginResponse;
import com.hcmute.shopfee.payload.response.RefreshEmployeeTokenResponse;
import com.hcmute.shopfee.enums.EmployeeRole;

import java.util.concurrent.ExecutionException;

public interface IEmployeeAuthService {
    EmployeeLoginResponse employeeLogin(EmployeeLoginRequest body) throws ExecutionException, InterruptedException;
    void employeeLogout(EmployeeLogoutRequest body, String refreshToken);
    RefreshEmployeeTokenResponse refreshEmployeeToken(String refreshToken);
    void employeeRegister(CreateEmployeeRequest body, EmployeeRole employeeRole);
    void changePasswordProfile(ChangePasswordEmployeeRequest data, String emplId);
    void setPasswordByEmployeeId(SetPasswordByEmployeeIdRequest data, String emplId);
}
