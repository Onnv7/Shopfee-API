package com.hcmute.shopfee.service.core.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.dto.kafka.CodeEmailDto;
import com.hcmute.shopfee.dto.request.*;
import com.hcmute.shopfee.dto.response.LoginResponse;
import com.hcmute.shopfee.dto.response.RefreshTokenResponse;
import com.hcmute.shopfee.dto.response.RegisterResponse;
import com.hcmute.shopfee.entity.sql.database.ConfirmationEntity;
import com.hcmute.shopfee.entity.sql.database.UserFCMTokenEntity;
import com.hcmute.shopfee.entity.sql.database.RoleEntity;
import com.hcmute.shopfee.entity.sql.database.UserEntity;
import com.hcmute.shopfee.enums.ConfirmationCodeStatus;
import com.hcmute.shopfee.enums.Role;
import com.hcmute.shopfee.enums.UserStatus;
import com.hcmute.shopfee.kafka.KafkaMessagePublisher;
import com.hcmute.shopfee.model.CustomException;
import com.hcmute.shopfee.entity.redis.UserTokenEntity;
import com.hcmute.shopfee.repository.database.ConfirmationRepository;
import com.hcmute.shopfee.repository.database.UserFCMTokenRepository;
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
import jakarta.servlet.http.HttpServletRequest;
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

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    private final UserFCMTokenRepository userFcmTokenRepository;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    private void updateFcmTokenById(String fcmTokenId, UserEntity user) {
        if(fcmTokenId == null) {
            return;
        }
        UserFCMTokenEntity userFcmTokenEntity = userFcmTokenRepository.findById(fcmTokenId)
                        .orElseThrow(() -> new CustomException(NOT_FOUND,FCM_TOKEN_ID_NOT_FOUND + fcmTokenId));
        userFcmTokenEntity.setUser(user);
        userFcmTokenRepository.save(userFcmTokenEntity);
    }

    @Transactional
    @Override
    public RegisterResponse registerUser(RegisterUserRequest body) {
        UserEntity userEntity = modelMapperService.mapClass(body, UserEntity.class);

        if (userRepository.findByEmail(userEntity.getEmail()).orElse(null) != null) {
            throw new CustomException(EXISTED_DATA, "Email already registered");
        }

        ConfirmationEntity confirmation = confirmationRepository.findByEmailAndCode(body.getEmail(), body.getCode())
                .orElseThrow(() -> new CustomException(UNAUTHORIZED, "Email has not been verified"));
        if (confirmation.getStatus() != ConfirmationCodeStatus.USED) {
            throw new CustomException(UNAUTHORIZED, "Email has not been verified");
        }
        confirmationRepository.delete(confirmation);

        RegisterResponse resData = new RegisterResponse();
        modelMapperService.map(userEntity, resData);

        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        Set<RoleEntity> roleList = new HashSet<>();
        RoleEntity userRole = roleRepository.findByRoleName(Role.ROLE_USER).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "Role with name " + Role.ROLE_USER));
        roleList.add(userRole);
        userEntity.setRoleList(roleList);
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity.setCoin(0L);
        userEntity = userRepository.save(userEntity);
        List<String> roleNameList = roleList.stream().map(it -> it.getRoleName().name()).toList();
        var accessToken = jwtService.issueAccessToken(userEntity.getId(), userEntity.getEmail(), roleNameList);
        var refreshToken = jwtService.issueRefreshToken(userEntity.getId(), userEntity.getEmail(), roleNameList);
        userTokenRedisService.createNewUserRefreshToken(refreshToken, userEntity.getId());
        resData.setAccessToken(accessToken);
        resData.setRefreshToken(refreshToken);
        resData.setUserId(userEntity.getId());
        updateFcmTokenById(body.getFcmTokenId(), userEntity);
        return resData;
    }

    @Override
    public RegisterResponse firebaseRegisterUser(FirebaseRegisterRequest body, HttpServletRequest request) {
        RegisterResponse resData = new RegisterResponse();
        String idToken = request.getHeader("Id-token");
        if (idToken == null) {
            throw new CustomException(NOT_FOUND, "Id token is null");
        }
        try {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);

            String displayName = decodedToken.getName();
            String[] nameParts = displayName.split("\\s+");
            String firstname = nameParts[0];
            String lastname = nameParts.length > 1 ? nameParts[nameParts.length - 1] : "";

            UserEntity userEntity = UserEntity.builder()
                    .email(decodedToken.getEmail())
                    .password(passwordEncoder.encode(decodedToken.getUid()))
                    .avatarUrl(decodedToken.getPicture())
                    .coin(0L)
                    .firstName(firstname)
                    .lastName(lastname)
                    .build();

            Set<RoleEntity> roleList = new HashSet<>();
            RoleEntity userRole = roleRepository.findByRoleName(Role.ROLE_USER)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "Role with name " + Role.ROLE_USER));
            roleList.add(userRole);
            userEntity.setRoleList(roleList);
            userEntity.setStatus(UserStatus.ACTIVE);

            userEntity = userRepository.save(userEntity);
            List<String> roleNameList = roleList.stream().map(it -> it.getRoleName().name()).toList();

            var accessToken = jwtService.issueAccessToken(userEntity.getId(), userEntity.getEmail(), roleNameList);
            var refreshToken = jwtService.issueRefreshToken(userEntity.getId(), userEntity.getEmail(), roleNameList);

            userTokenRedisService.createNewUserRefreshToken(refreshToken, userEntity.getId());

            resData.setAccessToken(accessToken);
            resData.setRefreshToken(refreshToken);
            resData.setUserId(userEntity.getId());
            updateFcmTokenById(body.getFcmTokenId(), userEntity);
            return resData;
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public LoginResponse userLogin(UserLoginRequest body) {
        UserPrincipal principal = UserPrincipal.builder()
                .username(body.getEmail())
                .password(body.getPassword())
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

        updateFcmTokenById(body.getFcmTokenId(), user);

        return LoginResponse.builder().accessToken(accessToken).userId(userId).refreshToken(refreshToken).build();
    }

    @Override
    public LoginResponse firebaseUserLogin(FirebaseLoginRequest body, HttpServletRequest request) {
        String idToken = request.getHeader("Id-token");
        if (idToken == null) {
            throw new CustomException(NOT_FOUND, "Id token is null");
        }
        try {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
            UserEntity user = userRepository.findByEmail(decodedToken.getEmail()).orElse(null);
            String userId = user.getId();
            String username = user.getEmail();

            List<String> roles = List.of(Role.ROLE_USER.name());

            var accessToken = jwtService.issueAccessToken(userId, username, roles);
            String refreshToken = jwtService.issueRefreshToken(userId, username, roles);
            userTokenRedisService.createNewUserRefreshToken(refreshToken, userId);
            updateFcmTokenById(body.getFcmTokenId(), user);
            return LoginResponse.builder().accessToken(accessToken).userId(userId).refreshToken(refreshToken).build();

        } catch (FirebaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void logoutUser(UserLogoutRequest body, String refreshToken) {
        String employeeId = SecurityUtils.getCurrentUserId();
        userTokenRedisService.deleteByUserIdAndRefreshToken(employeeId, refreshToken);
        UserFCMTokenEntity fcmTokenEntity = userFcmTokenRepository.findById(body.getFcmTokenId())
                .orElseThrow(() -> new CustomException(NOT_FOUND, FCM_TOKEN_ID_NOT_FOUND + body.getFcmTokenId()));
        fcmTokenEntity.setUser(null);
        userFcmTokenRepository.save(fcmTokenEntity);
    }

    @Override
    public void sendCodeToRegister(String email) {
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            throw new CustomException(EXISTED_DATA, "Email is already registered");
        }
        String code = GeneratorUtils.generateRandomCode(6);
        createOrUpdateConfirmationInfo(email, code);
        kafkaMessagePublisher.sendMessageToCodeEmail(new CodeEmailDto(code, email));
    }

    @Override
    public void sendCodeToGetPassword(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "User with email " + email));
        String code = GeneratorUtils.generateRandomCode(6);
        createOrUpdateConfirmationInfo(email, code);
        kafkaMessagePublisher.sendMessageToCodeEmail(new CodeEmailDto(code, email));
    }

    @Override
    public void verifyCodeByEmail(String code, String email) {
        ConfirmationEntity confirmationCollection = confirmationRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "Confirmation data with email " + email));
        Date currentTime = new Date();

        if (code.equals(confirmationCollection.getCode()) && currentTime.before(confirmationCollection.getExpireAt())) {
            confirmationCollection.setStatus(ConfirmationCodeStatus.USED);
            confirmationRepository.save(confirmationCollection);
            return;
        }

        throw new CustomException(UNAUTHORIZED, "Code is not valid");
    }

    @Transactional
    @Override
    public void changePasswordForgot(ChangePasswordRequest body) {
        UserEntity user = userRepository.findByEmail(body.getEmail())
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, "User with email " + body.getEmail()));

        ConfirmationEntity confirmation = confirmationRepository.findByEmailAndCode(body.getEmail(), body.getCode())
                .orElseThrow(() -> new CustomException(UNAUTHORIZED, "Email has not been verified"));
        if (confirmation.getStatus() != ConfirmationCodeStatus.USED) {
            throw new CustomException(UNAUTHORIZED, "Email has not been verified");
        }
        confirmationRepository.delete(confirmation);

        user.setPassword(passwordEncoder.encode(body.getPassword()));
        userRepository.save(user);
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        DecodedJWT jwt = jwtService.decodeRefreshToken(refreshToken);

        String userId = jwt.getSubject().toString();
        UserTokenEntity token = userTokenRedisService.getInfoOfRefreshToken(refreshToken, userId);

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, ErrorConstant.USER_ID_NOT_FOUND + userId));

        if (token == null) {
            throw new CustomException(NOT_FOUND, "There is no token data in the database");
        }
        if (token.isUsed()) {
            userTokenRedisService.deleteAllTokenByUserId(userId);
            throw new CustomException(FORBIDDEN, TOKEN_STOLEN);
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
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND, USER_ID_NOT_FOUND + userId));

        boolean isValid = passwordEncoder.matches(data.getOldPassword(), user.getPassword());

        if (!isValid) {
            throw new CustomException(UNAUTHORIZED, WRONG_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(data.getNewPassword()));
        userRepository.save(user);
    }

    public void createOrUpdateConfirmationInfo(String email, String code) {
        ConfirmationEntity oldConfirmation = confirmationRepository.findByEmail(email).orElse(null);
        Date currentDate = new Date();
        Instant instant = currentDate.toInstant();
        Instant newInstant = instant.plus(Duration.of(3, ChronoUnit.MINUTES));
        Date newDate = Date.from(newInstant);
        if (oldConfirmation == null) {
            ConfirmationEntity confirmation = ConfirmationEntity.builder()
                    .email(email)
                    .code(code)
                    .status(ConfirmationCodeStatus.UNUSED)
                    .expireAt(newDate)
                    .build();
            confirmationRepository.save(confirmation);
        } else {
            oldConfirmation.setExpireAt(newDate);
            oldConfirmation.setCode(code);
            confirmationRepository.save(oldConfirmation);
        }
    }

}
