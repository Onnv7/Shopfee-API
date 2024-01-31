package com.hcmute.shopfee.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseAPI<T> {
    private Date timestamp;
    @Builder.Default
    private boolean success = true;
    private String message;
    private T data;

}
