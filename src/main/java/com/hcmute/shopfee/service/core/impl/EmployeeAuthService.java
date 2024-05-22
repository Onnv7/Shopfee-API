package com.hcmute.shopfee.service.core.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.TopicManagementResponse;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.constant.ShopfeeConstant;
import com.hcmute.shopfee.dto.request.*;
import com.hcmute.shopfee.dto.response.EmployeeLoginResponse;
import com.hcmute.shopfee.dto.response.RefreshEmployeeTokenResponse;
import com.hcmute.shopfee.entity.sql.database.*;
import com.hcmute.shopfee.enums.EmployeeRole;
import com.hcmute.shopfee.enums.EmployeeStatus;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.ShopfeeException;
import com.hcmute.shopfee.repository.database.BranchRepository;
import com.hcmute.shopfee.repository.database.EmployeeFCMTokenRepository;
import com.hcmute.shopfee.repository.database.EmployeeRepository;
import com.hcmute.shopfee.security.UserPrincipal;
import com.hcmute.shopfee.security.custom.employee.EmployeeUsernamePasswordAuthenticationToken;
import com.hcmute.shopfee.service.core.IEmployeeAuthService;
import com.hcmute.shopfee.service.common.JwtService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.service.redis.EmployeeTokenRedisService;
import com.hcmute.shopfee.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.hcmute.shopfee.service.common.JwtService.ROLES_CLAIM_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeAuthService implements IEmployeeAuthService {
    private final EmployeeRepository employeeRepository;
    private final AuthenticationManager authenticationManager;
    private final EmployeeTokenRedisService employeeTokenRedisService;
    private final JwtService jwtService;
    private final ModelMapperService modelMapperService;
    private final BranchRepository branchRepository;
    private final EmployeeFCMTokenRepository employeeFCMTokenRepository;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;
    private void updateFcmTokenById(String fcmTokenId, EmployeeEntity employee) throws ExecutionException, InterruptedException {
        if(fcmTokenId == null) {
            return;
        }
        EmployeeFCMTokenEntity employeeFcmTokenEntity = employeeFCMTokenRepository.findById(fcmTokenId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.FCM_TOKEN_NOT_FOUND, ErrorConstant.NOT_FOUND  + fcmTokenId));
        employeeFcmTokenEntity.setEmployee(employee);
        employeeFCMTokenRepository.save(employeeFcmTokenEntity);

        TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopicAsync(
                Collections.singletonList(employeeFcmTokenEntity.getToken()),
                employee.getBranch().getId()
        ).get();
    }

    @Override
    public EmployeeLoginResponse employeeLogin(EmployeeLoginRequest body) throws ExecutionException, InterruptedException {
        UserPrincipal principal = UserPrincipal.builder()
                .username(body.getUsername())
                .password(body.getPassword())
                .build();
        Authentication employeeCredential = new EmployeeUsernamePasswordAuthenticationToken(principal);
        var authentication = authenticationManager.authenticate(employeeCredential);

        var principalAuthenticated = (UserPrincipal) authentication.getPrincipal();
        EmployeeEntity employee = employeeRepository.findByUsernameAndIsDeletedFalse(principalAuthenticated.getUsername()).orElse(null);

        if (employee.getStatus() == EmployeeStatus.INACTIVE) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.FORBIDDEN, "Your account is inactive");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        var roles = principalAuthenticated.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();

        var accessToken = jwtService.issueAccessToken(principalAuthenticated.getUserId(), principalAuthenticated.getUsername(), roles);
        String refreshToken = jwtService.issueRefreshToken(principalAuthenticated.getUserId(), principalAuthenticated.getUsername(), roles);

        employeeTokenRedisService.saveEmployeeToken(employee.getId(), refreshToken, false);
        updateFcmTokenById(body.getFcmTokenId(), employee);

        return EmployeeLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .branchId(employee.getBranch() != null ? employee.getBranch().getId() : null)
                .employeeId(principalAuthenticated.getUserId())
                .build();

    }

    @Override
    public void employeeLogout(EmployeeLogoutRequest body, String refreshToken) {
        String employeeId = SecurityUtils.getCurrentUserId();
        try {
            if(body.getFcmTokenId() != null) {
                EmployeeFCMTokenEntity fcmTokenEntity = employeeFCMTokenRepository.findById(body.getFcmTokenId())
                        .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.FCM_TOKEN_NOT_FOUND, ErrorConstant.NOT_FOUND  + body.getFcmTokenId()));
                fcmTokenEntity.setEmployee(null);
                employeeFCMTokenRepository.save(fcmTokenEntity);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public RefreshEmployeeTokenResponse refreshEmployeeToken(String refreshToken) {
        DecodedJWT jwt = jwtService.decodeRefreshToken(refreshToken);

        String employeeId = jwt.getSubject().toString();
        Boolean tokenInfoUsed = employeeTokenRedisService.getEmployeeTokenValue(employeeId, refreshToken);

        EmployeeEntity user = employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.EMPLOYEE_NOT_FOUND, ErrorConstant.NOT_FOUND + employeeId));

        if (tokenInfoUsed == null) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.UNAUTHORIZED, ShopfeeConstant.TOKEN_NOT_FOUND_ERR_MSG);
        }
        if (tokenInfoUsed) {
            employeeTokenRedisService.deleteAllTokenOfEmployee(employeeId);
            throw new ShopfeeException(ShopfeeErrorCode.TOKEN_STOLEN);
        }

        List<String> roles = jwt.getClaim(ROLES_CLAIM_KEY).asList(String.class);

        String newAccessToken = jwtService.issueAccessToken(user.getId(), user.getUsername(), roles);
        String newRefreshToken = jwtService.issueRefreshToken(user.getId(), user.getUsername(), roles);

        employeeTokenRedisService.saveEmployeeToken(employeeId, refreshToken, true);
        employeeTokenRedisService.saveEmployeeToken(employeeId, newRefreshToken, false);

        RefreshEmployeeTokenResponse resData = RefreshEmployeeTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
        return resData;
    }

    @Override
    public void employeeRegister(CreateEmployeeRequest body, EmployeeRole employeeRoleName) {
        String employeeId = SecurityUtils.getCurrentUserId();
        EmployeeEntity creator = employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.SupErrorCode.UNAUTHORIZED));

        if(employeeRoleName == EmployeeRole.ROLE_ADMIN && creator.getRole() != EmployeeRole.ROLE_ADMIN) {
            throw new ShopfeeException(ShopfeeErrorCode.CANT_CREATE_ADMIN_ACCOUNT);
        }
        // manager không thể tạo manager khác
        if(creator.getRole() == EmployeeRole.ROLE_MANAGER && employeeRoleName == EmployeeRole.ROLE_MANAGER) {
            throw new ShopfeeException(ShopfeeErrorCode.CANT_CREATE_MANAGER_ACCOUNT);
        }

        if(creator.getRole() == EmployeeRole.ROLE_MANAGER) {
            // manager không được tạo emlpyee cho chi nhánh khác
            if(!creator.getBranch().getId().equals(body.getBranchId())) {
                throw new ShopfeeException(ShopfeeErrorCode.CANT_CREATE_EMPLOYEE_ACCOUNT_OF_ANOTHER_BRANCH);
            }
        }

        // TODO: kiểm tra manager của branch tồn tại chưa

        EmployeeEntity employeeData = modelMapperService.mapClass(body, EmployeeEntity.class);

        EmployeeEntity existedEmployee = employeeRepository.findByUsername(employeeData.getUsername()).orElse(null);
        if (existedEmployee != null) {
            throw new ShopfeeException(ShopfeeErrorCode.SupErrorCode.EXISTED_DATA, "Username account registered");
        }

        employeeData.setRole(employeeRoleName);
        if(employeeRoleName != EmployeeRole.ROLE_ADMIN) {
            BranchEntity branch = branchRepository.findById(String.valueOf(body.getBranchId()))
                    .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.BRANCH_NOT_FOUND, ErrorConstant.NOT_FOUND + body.getBranchId()));
            employeeData.setBranch(branch);
        }

        employeeData.setPassword(passwordEncoder.encode(employeeData.getPassword()));
        employeeData.setStatus(EmployeeStatus.ACTIVE);
        employeeRepository.save(employeeData);
    }

    @Override
    public void changePasswordProfile(ChangePasswordEmployeeRequest data, String emplId) {
        SecurityUtils.checkUserId(emplId);
        EmployeeEntity employee = employeeRepository.findByIdAndIsDeletedFalse(emplId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.EMPLOYEE_NOT_FOUND, ErrorConstant.NOT_FOUND + emplId));
        boolean isValid = passwordEncoder.matches(data.getOldPassword(), employee.getPassword());
        if (!isValid) {
            throw new ShopfeeException(ShopfeeErrorCode.WRONG_PASSWORD);
        }
        employee.setPassword(passwordEncoder.encode(data.getNewPassword()));
        employeeRepository.save(employee);
    }

    // TODO: check quyeefn admin va branch cho viec doi mk
    @Override
    public void setPasswordByEmployeeId(SetPasswordByEmployeeIdRequest data, String emplId) {
        EmployeeEntity employee = employeeRepository.findByIdAndIsDeletedFalse(emplId)
                .orElseThrow(() -> new ShopfeeException(ShopfeeErrorCode.EMPLOYEE_NOT_FOUND, ErrorConstant.NOT_FOUND + emplId));
                employee.setPassword(passwordEncoder.encode(data.getPassword()));
        employeeRepository.save(employee);
    }
}
