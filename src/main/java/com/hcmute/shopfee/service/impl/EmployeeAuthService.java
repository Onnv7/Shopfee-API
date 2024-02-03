package com.hcmute.shopfee.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.request.ChangePasswordEmployeeRequest;
import com.hcmute.shopfee.dto.request.CreateEmployeeRequest;
import com.hcmute.shopfee.dto.response.EmployeeLoginResponse;
import com.hcmute.shopfee.dto.response.RefreshEmployeeTokenResponse;
import com.hcmute.shopfee.entity.BranchEntity;
import com.hcmute.shopfee.entity.EmployeeEntity;
import com.hcmute.shopfee.enums.EmployeeStatus;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.model.redis.EmployeeToken;
import com.hcmute.shopfee.repository.database.BranchRepository;
import com.hcmute.shopfee.repository.database.EmployeeRepository;
import com.hcmute.shopfee.security.UserPrincipal;
import com.hcmute.shopfee.security.custom.employee.EmployeeUsernamePasswordAuthenticationToken;
import com.hcmute.shopfee.service.IEmployeeAuthService;
import com.hcmute.shopfee.service.common.JwtService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.service.redis.EmployeeRefreshTokenRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.hcmute.shopfee.constant.ErrorConstant.*;
import static com.hcmute.shopfee.service.common.JwtService.ROLES_CLAIM_KEY;

@Service
@RequiredArgsConstructor
public class EmployeeAuthService implements IEmployeeAuthService {
    private final EmployeeRepository employeeRepository;
    private final AuthenticationManager authenticationManager;
    private final EmployeeRefreshTokenRedisService employeeRefreshTokenRedisService;
    private final JwtService jwtService;
    private final ModelMapperService modelMapperService;
    private final BranchRepository branchRepository;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public EmployeeLoginResponse attemptEmployeeLogin(String username, String password) {
        UserPrincipal principal = UserPrincipal.builder()
                .username(username)
                .password(password)
                .build();
        Authentication employeeCredential = new EmployeeUsernamePasswordAuthenticationToken(principal);
        var authentication = authenticationManager.authenticate(employeeCredential);

        var principalAuthenticated = (UserPrincipal) authentication.getPrincipal();
        EmployeeEntity employee = employeeRepository.findByUsernameAndIsDeletedFalse(principalAuthenticated.getUsername()).orElse(null);

        if (employee.getStatus() == EmployeeStatus.INACTIVE) {
            throw new CustomException(ErrorConstant.ACCOUNT_BLOCKED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        var roles = principalAuthenticated.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();

        var accessToken = jwtService.issueAccessToken(principalAuthenticated.getUserId(), principalAuthenticated.getUsername(), roles);
        String refreshToken = jwtService.issueRefreshToken(principalAuthenticated.getUserId(), principalAuthenticated.getUsername(), roles);

        employeeRefreshTokenRedisService.createNewEmployeeRefreshToken(refreshToken, principalAuthenticated.getUserId());
        return EmployeeLoginResponse.builder().accessToken(accessToken).refreshToken(refreshToken).employeeId(principalAuthenticated.getUserId()).build();

    }

    @Override
    public RefreshEmployeeTokenResponse refreshEmployeeToken(String refreshToken) {
        DecodedJWT jwt = jwtService.decodeRefreshToken(refreshToken);

        String userId = jwt.getSubject().toString();
        EmployeeToken token = employeeRefreshTokenRedisService.getInfoOfRefreshToken(refreshToken, userId);

        EmployeeEntity user = employeeRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + userId));

        if (token == null) {
            throw new CustomException(INVALID_TOKEN);
        }
        if (token.isUsed()) {
            employeeRefreshTokenRedisService.deleteUserRefreshToken(userId);
            throw new CustomException(STOLEN_TOKEN);
        }

        List<String> roles = jwt.getClaim(ROLES_CLAIM_KEY).asList(String.class);

        String newAccessToken = jwtService.issueAccessToken(user.getId(), user.getUsername(), roles);
        String newRefreshToken = jwtService.issueRefreshToken(user.getId(), user.getUsername(), roles);
        employeeRefreshTokenRedisService.updateUsedEmployeeRefreshToken(token);
        employeeRefreshTokenRedisService.createNewEmployeeRefreshToken(newRefreshToken, userId);


        RefreshEmployeeTokenResponse resData = RefreshEmployeeTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
        return resData;
    }

    @Override
    public void registerEmployee(CreateEmployeeRequest body) {
        EmployeeEntity data = modelMapperService.mapClass(body, EmployeeEntity.class);
        EmployeeEntity existedEmployee = employeeRepository.findByUsernameAndIsDeletedFalse(data.getUsername()).orElse(null);
        if (existedEmployee != null) {
            throw new CustomException(ErrorConstant.REGISTERED_EMAIL);
        }
        BranchEntity branch = branchRepository.findById(body.getBranchId()).orElseThrow(() -> new CustomException(NOT_FOUND + body.getBranchId()));
        data.setBranch(branch);
        data.setPassword(passwordEncoder.encode(data.getPassword()));
//        data.setCode(sequenceService.generateCode(EmployeeCollection.SEQUENCE_NAME, EmployeeCollection.PREFIX_CODE, EmployeeCollection.LENGTH_NUMBER));
        data.setStatus(EmployeeStatus.ACTIVE);
        employeeRepository.save(data);
    }

    @Override
    public void changePasswordProfile(ChangePasswordEmployeeRequest data, String emplId) {
        EmployeeEntity employee = employeeRepository.findByUsernameAndIsDeletedFalse(emplId)
                .orElseThrow(() -> new CustomException(NOT_FOUND + emplId));
        boolean isValid = passwordEncoder.matches(data.getOldPassword(), employee.getPassword());
        if (!isValid) {
            throw new CustomException(ErrorConstant.INVALID_PASSWORD);
        }
        employee.setPassword(passwordEncoder.encode(data.getNewPassword()));
        employeeRepository.save(employee);
    }
}
