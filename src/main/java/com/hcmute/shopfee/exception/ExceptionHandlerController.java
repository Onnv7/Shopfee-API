package com.hcmute.shopfee.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import com.hcmute.shopfee.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.List;

import static com.hcmute.shopfee.constant.ErrorConstant.*;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {
    @Value("${spring.profiles.active}")
    private String environment;
    private String dev = "dev";
    private String prod = "prod";

    private static final List<String> error404 = List.of(NOT_FOUND);
    private static final List<String> error400 = Arrays.asList(CANT_DELETE, DATA_SEND_INVALID, ACTING_INCORRECTLY, EXISTED_DATA);
    private static final List<String> error403 = List.of(FORBIDDEN);
    private static final List<String> error401 = List.of(UNAUTHORIZED);
    private static final List<String> error500 = List.of(SERVER_ERROR);


    @ExceptionHandler({AuthenticationException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception ex) {
        ex.printStackTrace();
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        ErrorResponse<Object> res = new ErrorResponse<>();
        ErrorResponse.ErrorData errorData = null;
        if (ex instanceof AuthenticationException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
            errorData = new ErrorResponse.ErrorData(ShopfeeErrorCode.SupErrorCode.UNAUTHORIZED);

        } else if (ex instanceof AccessDeniedException) {
            httpStatus = HttpStatus.FORBIDDEN;
            errorData = new ErrorResponse.ErrorData(ShopfeeErrorCode.SupErrorCode.FORBIDDEN);
        }
        res.setError(errorData);
        if (environment.equals(dev)) {
            ErrorResponse.DevResponse<Object> devResponse = ErrorResponse.DevResponse.builder()
                    .message(ex.getMessage())
                    .build();
            res.setDevResponse(devResponse);
        }
        return new ResponseEntity<>(res, httpStatus);
    }

    @ExceptionHandler({ShopfeeException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse<?>> handleCustomException(Exception ex) {
        ex.printStackTrace();
        ErrorResponse<Object> res = new ErrorResponse<>();

        if (ex instanceof MethodArgumentNotValidException) {
            ex.printStackTrace();
            ErrorResponse.ErrorData errorData = ErrorResponse.ErrorData.builder()
                    .errorCode(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID.code())
                    .errorMessage(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID.description())
                    .subErrorCode(null)
                    .subErrorMessage(null)
                    .build();
            res.setError(errorData);
            if (environment.equals(dev)) {
                ErrorResponse.DevResponse devResponse = getDetailDataInvalid((MethodArgumentNotValidException) ex);
                res.setDevResponse(devResponse);
            }
        } else if (ex instanceof ShopfeeException) {

            ShopfeeErrorCode subError = ((ShopfeeException) ex).getSubError();

            ErrorResponse.ErrorData errorData = ErrorResponse.ErrorData.builder()
                    .errorCode(((ShopfeeException) ex).getError().code())
                    .errorMessage(((ShopfeeException) ex).getError().description())
                    .subErrorCode(subError != null ? subError.code() : null)
                    .subErrorMessage(subError != null ? subError.description() : null)
                    .build();

            res.setError(errorData);
            if (environment.equals(dev)) {

                ErrorResponse.DevResponse devResponse = new ErrorResponse.DevResponse();
                devResponse.setMessage(((ShopfeeException) ex).getDevMessage());
                res.setDevResponse(devResponse);
                res.setErrorCode(((ShopfeeException) ex).getErrorCode());
            }
        }
        HttpStatus httpStatus = getHttpStatus(res.getError().getErrorMessage());
        return new ResponseEntity<ErrorResponse<?>>(res, httpStatus);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ex.printStackTrace();
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse.ErrorData errorData =  new ErrorResponse.ErrorData(ShopfeeErrorCode.SupErrorCode.SERVER_ERROR);
        ErrorResponse res = new ErrorResponse();
        if (ex instanceof AuthenticationException || ex instanceof JWTVerificationException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
            errorData = new ErrorResponse.ErrorData(ShopfeeErrorCode.SupErrorCode.UNAUTHORIZED);
        } else if (ex instanceof AccessDeniedException) {
            httpStatus = HttpStatus.FORBIDDEN;
            errorData = new ErrorResponse.ErrorData(ShopfeeErrorCode.SupErrorCode.FORBIDDEN);
        } else if (ex instanceof MissingServletRequestParameterException || ex instanceof MethodArgumentTypeMismatchException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            errorData = new ErrorResponse.ErrorData(ShopfeeErrorCode.SupErrorCode.DATA_SEND_INVALID);
        }

        res.setError(errorData);

        if (environment.equals(dev)) {
            ErrorResponse.DevResponse devResponse = ErrorResponse.DevResponse.builder()
                    .message(ex.getMessage())
                    .build();
            res.setDevResponse(devResponse);
        }
        return new ResponseEntity<>(res, httpStatus);
    }

    private static HttpStatus getHttpStatus(String exMessage) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (error404.contains(exMessage)) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (error400.contains(exMessage)) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (error401.contains(exMessage)) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (error403.contains(exMessage)) {
            httpStatus = HttpStatus.FORBIDDEN;
        }
        return httpStatus;
    }

    private static ErrorResponse.DevResponse getDetailDataInvalid(MethodArgumentNotValidException ex) {
        ErrorResponse.DevResponse devResponse = ErrorResponse.DevResponse.builder()
                .details(ex.getFieldErrors().stream().map(it -> FieldError.builder()
                        .field(it.getField())
                        .valueReject(it.getRejectedValue())
                        .validate(it.getDefaultMessage())
                        .build()).toList())
                .build();
        return devResponse;
    }

}