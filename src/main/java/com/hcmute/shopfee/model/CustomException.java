package com.hcmute.shopfee.model;

import lombok.Data;

@Data
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
    private String detailMessage;
    private String errorCode;
    public CustomException(String message, String detailMessage) {
        super(message);
        this.detailMessage = detailMessage;
    }

    public CustomException(String message, String detailMessage, String errorCode) {
        super(message);
        this.detailMessage = detailMessage;
        this.errorCode = errorCode;
    }
}
