package com.hcmute.shopfee.exception;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.constant.StatusCode;
import com.hcmute.shopfee.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;

import static com.hcmute.shopfee.constant.ErrorConstant.*;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {
    @Value("spring.profile.active")
    private String environment;
    private String dev = "dev";
    private String prod = "prod";

    private static final List<String> error404= Arrays.asList(
            USER_NOT_FOUND,
            EMPLOYEE_NOT_FOUND,
            NOT_FOUND,
            CATEGORY_NOT_FOUND,
            PRODUCT_NOT_FOUND
    );
    private static final List<String> error400= Arrays.asList(
            REGISTERED_EMAIL, INVALID_COIN_NUMBER, IMAGE_INVALID,
            PARAMETER_INVALID, PRODUCT_NAME_EXISTED, COUPON_STATUS_UNRELEASED,
            TOTAL_ORDER_INVALID, COUPON_EXPIRED, COUPON_INVALID, CANT_DELETE, REQUEST_BODY_INVALID,
            OVER_FIVE_ADDRESS
    );
    private static final List<String> error403= Arrays.asList(
            PRINCIPAL_INVALID, USER_ID_INVALID, ACCOUNT_BLOCKED,
            FORBIDDEN
    );
    private static final List<String> error401= Arrays.asList(
            INVALID_PASSWORD, STOLEN_TOKEN, INVALID_TOKEN
    );


    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        ex.printStackTrace();
        ErrorResponse res = ErrorResponse.builder()
                .message(ex.getMessage())
                .stack(environment.equals(dev) ? Arrays.toString(ex.getStackTrace()) : null)
                .build();
        return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse<?>> handleCustomException(CustomException ex) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ex.printStackTrace();
        if(error404.contains(ex.getMessage())) {
            httpStatus = StatusCode.NOT_FOUND;
        } else if(error400.contains(ex.getMessage())) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if(error401.contains(ex.getMessage())) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        }else if(error403.contains(ex.getMessage())) {
            httpStatus = HttpStatus.FORBIDDEN;
        }
        ErrorResponse res = ErrorResponse.builder()
                .message(ex.getMessage())
                .stack(environment.equals(dev) ? Arrays.toString(ex.getStackTrace()) : null)
                .build();
        return new ResponseEntity<ErrorResponse<?>>(res, httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ex.printStackTrace();

        ErrorResponse res = ErrorResponse.builder()
                .message(ErrorConstant.REQUEST_BODY_INVALID)
                .details(
                        ex.getFieldErrors().stream()
                                .map(
                                        it-> FieldError.builder()
                                                .field(it.getField())
                                                .valueReject(it.getRejectedValue())
                                                .validate(it.getDefaultMessage())
                                                .build()
                                ).toList()
                )
                .build();
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ex.printStackTrace();
        HttpStatus httpStatus =  HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse res = new ErrorResponse();
        if(ex instanceof  BadCredentialsException) {
            res.setMessage(ex.getMessage());
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if(ex instanceof AccessDeniedException) {
            res.setMessage(ex.getMessage());
            httpStatus = HttpStatus.FORBIDDEN;
        } else if(ex instanceof SignatureVerificationException) {
            res.setMessage(ex.getMessage());
            httpStatus = HttpStatus.FORBIDDEN;
//        } else if(ex instanceof ) {
//            res.setMessage(ex.getMessage());
//            httpStatus = HttpStatus.FORBIDDEN;
        } else {
            res = ErrorResponse.builder()
                    .message(ex.getMessage())
                    .stack(environment.equals(dev) ? Arrays.toString(ex.getStackTrace()) : null)
                    .build();
        }
        return new ResponseEntity<>(res, httpStatus);
    }
}