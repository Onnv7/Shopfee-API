package com.hcmute.shopfee.exception;

import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
public class TestError extends MethodArgumentNotValidException{
    public TestError(MethodParameter parameter, BindingResult bindingResult) {
        super(parameter, bindingResult);
    }
}
