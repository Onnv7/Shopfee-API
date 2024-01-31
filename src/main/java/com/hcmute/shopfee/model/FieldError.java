package com.hcmute.shopfee.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldError {
    private String field;
    private Object valueReject;
    private String validate;
}
