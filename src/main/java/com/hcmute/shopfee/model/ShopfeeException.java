package com.hcmute.shopfee.model;

import com.hcmute.shopfee.constant.ErrorConstant;
import lombok.Data;

@Data
public class ShopfeeException extends RuntimeException{
    public ShopfeeException(String message) {
        super(message);
    }
    private String devMessage;
    private int errorCode;
    public ShopfeeException(String message, String devMessage) {
        super(message);
        this.devMessage = devMessage;
        this.errorCode = ErrorConstant.getErrorCode(message);
    }

//    public CustomException(String message, String detailMessage, String devMessage) {
//        super(message);
//        this.detailMessage = detailMessage;
//        this.devMessage = devMessage;
//    }
    public ShopfeeException(String message, String devMessage, int errorCode) {
        super(message);
        this.devMessage = devMessage;
        this.errorCode = errorCode;
    }
}
