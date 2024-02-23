package com.hcmute.shopfee.service.core.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.kafka.CodeEmailDto;
import com.hcmute.shopfee.dto.request.RegisterUserRequest;
import com.hcmute.shopfee.dto.request.UpdatePasswordRequest;
import com.hcmute.shopfee.dto.response.LoginResponse;
import com.hcmute.shopfee.dto.response.RefreshTokenResponse;
import com.hcmute.shopfee.dto.response.RegisterResponse;
import com.hcmute.shopfee.entity.database.ConfirmationEntity;
import com.hcmute.shopfee.entity.database.RoleEntity;
import com.hcmute.shopfee.entity.database.UserEntity;
import com.hcmute.shopfee.enums.Role;
import com.hcmute.shopfee.kafka.KafkaMessagePublisher;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.entity.redis.UserTokenEntity;
import com.hcmute.shopfee.repository.database.ConfirmationRepository;
import com.hcmute.shopfee.repository.database.RoleRepository;
import com.hcmute.shopfee.repository.database.UserRepository;
import com.hcmute.shopfee.security.UserPrincipal;
import com.hcmute.shopfee.security.custom.user.UserUsernamePasswordAuthenticationToken;
import com.hcmute.shopfee.service.core.IUserAuthService;
import com.hcmute.shopfee.service.common.JwtService;
import com.hcmute.shopfee.service.common.ModelMapperService;
import com.hcmute.shopfee.service.redis.UserTokenRedisService;
import com.hcmute.shopfee.utils.GeneratorUtils;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.hcmute.shopfee.constant.ErrorConstant.*;
import static com.hcmute.shopfee.service.common.JwtService.ROLES_CLAIM_KEY;

@Service
@RequiredArgsConstructor
public class UserAuthService implements IUserAuthService {
    private final ModelMapperService modelMapperService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final UserTokenRedisService userTokenRedisService;
    private final ConfirmationRepository confirmationRepository;
    private final KafkaMessagePublisher kafkaMessagePublisher;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public RegisterResponse registerUser(RegisterUserRequest body) {
        UserEntity userEntity = modelMapperService.mapClass(body, UserEntity.class);

        if (userRepository.findByEmail(userEntity.getEmail()).orElse(null) != null) {
            throw new CustomException(ErrorConstant.REGISTERED_EMAIL);
        }

        RegisterResponse resData = new RegisterResponse();
        modelMapperService.map(userEntity, resData);

//        data.setRoles(new Role[]{Role.ROLE_USER});
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
//        data.setCode(sequenceService.generateCode(UserCollection.SEQUENCE_NAME, UserCollection.PREFIX_CODE, UserCollection.LENGTH_NUMBER));

        Set<RoleEntity> roleList = new HashSet<>();
        RoleEntity userRole = roleRepository.findByRoleName(Role.ROLE_USER).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + Role.ROLE_USER));
        roleList.add(userRole);
        userEntity.setRoleList(roleList);
        userEntity.setEnabled(true);
        UserEntity savedUser = userRepository.save(userEntity);
        List<String> roleNameList = roleList.stream().map(it -> it.getRoleName().name()).toList();
        var accessToken = jwtService.issueAccessToken(savedUser.getId(), savedUser.getEmail(), roleNameList);
        var refreshToken = jwtService.issueRefreshToken(savedUser.getId(), savedUser.getEmail(), roleNameList);
        userTokenRedisService.createNewUserRefreshToken(refreshToken, savedUser.getId());
        resData.setAccessToken(accessToken);
        resData.setRefreshToken(refreshToken);
        resData.setUserId(savedUser.getId());
        return resData;
    }

    @Override
    public LoginResponse userLogin(String email, String password) {
        UserPrincipal principal = UserPrincipal.builder()
                .username(email)
                .password(password)
                .build();

        Authentication userCredential = new UserUsernamePasswordAuthenticationToken(principal);
        var authentication = authenticationManager.authenticate(userCredential);

        var principalAuthenticated = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userRepository.findByEmail(principalAuthenticated.getUsername()).orElse(null);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        var roles = authentication.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();
        String userId = principalAuthenticated.getUserId();
        String username = principalAuthenticated.getUsername();
        var accessToken = jwtService.issueAccessToken(userId, username, roles);
        String refreshToken = jwtService.issueRefreshToken(userId, username, roles);

        userTokenRedisService.createNewUserRefreshToken(refreshToken, principalAuthenticated.getUserId());
        return LoginResponse.builder().accessToken(accessToken).userId(userId).refreshToken(refreshToken).build();
    }

    @Override
    public void logoutUser(String refreshToken) {
        String employeeId = SecurityUtils.getCurrentUserId();
        userTokenRedisService.deleteByUserIdAndRefreshToken(employeeId, refreshToken);
    }

    @Override
    public void resendCode(String email) {
        ConfirmationEntity confirmation = confirmationRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + email));


        String code = GeneratorUtils.generateRandomCode(6);
        confirmation.setCode(code);
        confirmationRepository.save(confirmation);
        kafkaMessagePublisher.sendMessageToCodeEmail(new CodeEmailDto(code, email));
    }

    @Override
    public void sendCodeToRegister(String email) {
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            throw new CustomException(ErrorConstant.REGISTERED_EMAIL);
        }
        String code = GeneratorUtils.generateRandomCode(6);
        createOrUpdateConfirmationInfo(email, code);
        kafkaMessagePublisher.sendMessageToCodeEmail(new CodeEmailDto(code, email));
    }

    @Override
    public void sendCodeToGetPassword(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + email));
        String code = GeneratorUtils.generateRandomCode(6);
        createOrUpdateConfirmationInfo(email, code);
        kafkaMessagePublisher.sendMessageToCodeEmail(new CodeEmailDto(code, email));
    }

    @Override
    public void verifyCodeByEmail(String code, String email) {
        ConfirmationEntity confirmationCollection = confirmationRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + email));
        if (confirmationCollection != null && code.equals(confirmationCollection.getCode())) {
            confirmationRepository.deleteById(confirmationCollection.getId());
            return;
        }

        throw new CustomException(ErrorConstant.EMAIL_UNVERIFIED);
    }

    @Override
    public void changePasswordForgot(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + email));
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        DecodedJWT jwt = jwtService.decodeRefreshToken(refreshToken);


        String userId = jwt.getSubject().toString();
        UserTokenEntity token = userTokenRedisService.getInfoOfRefreshToken(refreshToken, userId);

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + userId));

        if (token == null) {
            throw new CustomException(INVALID_TOKEN);
        }
        if (token.isUsed()) {
            userTokenRedisService.deleteAllTokenByUserId(userId);
            throw new CustomException(STOLEN_TOKEN);
        }
        List<String> roles = jwt.getClaim(ROLES_CLAIM_KEY).asList(String.class);
        String newAccessToken = jwtService.issueAccessToken(user.getId(), user.getEmail(), roles);
        String newRefreshToken = jwtService.issueRefreshToken(user.getId(), user.getEmail(), roles);
        userTokenRedisService.createNewUserRefreshToken(newAccessToken, userId);
        userTokenRedisService.updateUsedUserRefreshToken(token);


        RefreshTokenResponse resData = RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
        return resData;
    }

    @Override
    public void changePasswordProfile(String userId, UpdatePasswordRequest data) {
        SecurityUtils.checkUserId(userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND + userId));

        boolean isValid = passwordEncoder.matches(data.getOldPassword(), user.getPassword());

        if (!isValid) {
            throw new CustomException(ErrorConstant.INVALID_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(data.getNewPassword()));
        userRepository.save(user);
    }

    public void createOrUpdateConfirmationInfo(String email, String code) {
        ConfirmationEntity oldConfirmation = confirmationRepository.findByEmail(email).orElse(null);
        if(oldConfirmation == null) {
            ConfirmationEntity confirmation = ConfirmationEntity.builder()
                    .email(email)
                    .code(code)
                    .build();
            confirmationRepository.save(confirmation);
        }
        else {
            oldConfirmation.setCode(code);
            confirmationRepository.save(oldConfirmation);
        }
    }

}
