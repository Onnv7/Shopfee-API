package com.hcmute.shopfee.service.core.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.TopicManagementResponse;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.ChangePasswordEmployeeRequest;
import com.hcmute.shopfee.dto.request.CreateEmployeeRequest;
import com.hcmute.shopfee.dto.request.EmployeeLoginRequest;
import com.hcmute.shopfee.dto.request.EmployeeLogoutRequest;
import com.hcmute.shopfee.dto.response.EmployeeLoginResponse;
import com.hcmute.shopfee.dto.response.RefreshEmployeeTokenResponse;
import com.hcmute.shopfee.entity.sql.database.*;
import com.hcmute.shopfee.enums.EmployeeStatus;
import com.hcmute.shopfee.enums.Role;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.entity.redis.EmployeeTokenEntity;
import com.hcmute.shopfee.repository.database.BranchRepository;
import com.hcmute.shopfee.repository.database.EmployeeFCMTokenRepository;
import com.hcmute.shopfee.repository.database.EmployeeRepository;
import com.hcmute.shopfee.repository.database.RoleRepository;
import com.hcmute.shopfee.security.UserPrincipal;
import com.hcmute.shopfee.security.custom.employee.EmployeeUsernamePasswordAuthenticationToken;
import com.hcmute.shopfee.service.core.IEmployeeAuthService;
import com.hcmute.shopfee.service.common.JwtService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.service.redis.EmployeeTokenRedisService;
import com.hcmute.shopfee.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static com.hcmute.shopfee.constant.ErrorConstant.*;
import static com.hcmute.shopfee.service.common.JwtService.ROLES_CLAIM_KEY;

@Service
@RequiredArgsConstructor
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
    @Autowired
    private RoleRepository roleRepository;
    private void updateFcmTokenById(String fcmTokenId, EmployeeEntity employee) throws ExecutionException, InterruptedException {
        if(fcmTokenId == null) {
            return;
        }
        EmployeeFCMTokenEntity employeeFcmTokenEntity = employeeFCMTokenRepository.findById(fcmTokenId)
                .orElseThrow(() -> new CustomException(NOT_FOUND,FCM_TOKEN_ID_NOT_FOUND + fcmTokenId));
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
            throw new CustomException(ErrorConstant.FORBIDDEN, "Your account is inactive");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        var roles = principalAuthenticated.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();

        var accessToken = jwtService.issueAccessToken(principalAuthenticated.getUserId(), principalAuthenticated.getUsername(), roles);
        String refreshToken = jwtService.issueRefreshToken(principalAuthenticated.getUserId(), principalAuthenticated.getUsername(), roles);

        employeeTokenRedisService.createNewEmployeeRefreshToken(refreshToken, principalAuthenticated.getUserId());

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
        employeeTokenRedisService.deleteByEmployeeIdAndRefreshToken(employeeId, refreshToken);

        EmployeeFCMTokenEntity fcmTokenEntity = employeeFCMTokenRepository.findById(body.getFcmTokenId())
                .orElseThrow(() -> new CustomException(NOT_FOUND, FCM_TOKEN_ID_NOT_FOUND + body.getFcmTokenId()));
        fcmTokenEntity.setEmployee(null);
        employeeFCMTokenRepository.save(fcmTokenEntity);
    }

    @Override
    public RefreshEmployeeTokenResponse refreshEmployeeToken(String refreshToken) {
        DecodedJWT jwt = jwtService.decodeRefreshToken(refreshToken);

        String employeeId = jwt.getSubject().toString();
        EmployeeTokenEntity token = employeeTokenRedisService.getInfoOfRefreshToken(refreshToken, employeeId);

        EmployeeEntity user = employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.EMPLOYEE_ID_NOT_FOUND + employeeId));

        if (token == null) {
            throw new CustomException(UNAUTHORIZED, "Token is null");
        }
        if (token.isUsed()) {
            employeeTokenRedisService.deleteAllTokenByEmployeeId(employeeId);
            throw new CustomException(FORBIDDEN, TOKEN_STOLEN);
        }

        List<String> roles = jwt.getClaim(ROLES_CLAIM_KEY).asList(String.class);

        String newAccessToken = jwtService.issueAccessToken(user.getId(), user.getUsername(), roles);
        String newRefreshToken = jwtService.issueRefreshToken(user.getId(), user.getUsername(), roles);
        employeeTokenRedisService.updateUsedEmployeeRefreshToken(token);
        employeeTokenRedisService.createNewEmployeeRefreshToken(newRefreshToken, employeeId);

        RefreshEmployeeTokenResponse resData = RefreshEmployeeTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
        return resData;
    }

    @Override
    public void employeeRegister(CreateEmployeeRequest body, Role roleName) {
        List<String> roles = SecurityUtils.getRoleList();
        // manager không thể tạo manager khác
        if(!roles.contains(Role.ROLE_ADMIN.name()) && roleName == Role.ROLE_MANAGER) {
            throw new CustomException(ErrorConstant.FORBIDDEN, "Managers cannot create another manager account");
        }

        if(SecurityUtils.isOnlyRole(roles, Role.ROLE_MANAGER)) {
            EmployeeEntity manager =  employeeRepository.findByIdAndIsDeletedFalse(SecurityUtils.getCurrentUserId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, USER_ID_NOT_FOUND + SecurityUtils.getCurrentUserId()));
            // manager không được tạo emlpyee cho chi nhánh khác
            if(!manager.getBranch().getId().equals(body.getBranchId())) {
                throw new CustomException(ErrorConstant.FORBIDDEN, "Managers cannot create an employee account belonging to another branch");
            }
        }

        // TODO: kiểm tra manager của branch tồn tại chưa

        EmployeeEntity employeeData = modelMapperService.mapClass(body, EmployeeEntity.class);

        EmployeeEntity existedEmployee = employeeRepository.findByUsernameAndIsDeletedFalse(employeeData.getUsername()).orElse(null);
        if (existedEmployee != null) {
            throw new CustomException(ErrorConstant.EXISTED_DATA, "Username account registered");
        }

        Set<RoleEntity> employeeRole = new HashSet<>();
        RoleEntity role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new CustomException(NOT_FOUND, "Role with name" + roleName));
        employeeRole.add(role);
        employeeData.setRoleList(employeeRole);

        BranchEntity branch = branchRepository.findById(String.valueOf(body.getBranchId()))
                .orElseThrow(() -> new CustomException(NOT_FOUND, ErrorConstant.BRANCH_ID_NOT_FOUND + body.getBranchId()));

        employeeData.setBranch(branch);
        employeeData.setPassword(passwordEncoder.encode(employeeData.getPassword()));
        employeeData.setStatus(EmployeeStatus.ACTIVE);
        employeeRepository.save(employeeData);
    }

    @Override
    public void changePasswordProfile(ChangePasswordEmployeeRequest data, String emplId) {
        SecurityUtils.checkUserId(emplId);
        EmployeeEntity employee = employeeRepository.findByIdAndIsDeletedFalse(emplId)
                .orElseThrow(() -> new CustomException(NOT_FOUND, ErrorConstant.EMPLOYEE_ID_NOT_FOUND + emplId));
        boolean isValid = passwordEncoder.matches(data.getOldPassword(), employee.getPassword());
        if (!isValid) {
            throw new CustomException(ErrorConstant.UNAUTHORIZED, WRONG_PASSWORD);
        }
        employee.setPassword(passwordEncoder.encode(data.getNewPassword()));
        employeeRepository.save(employee);
    }
}
