package com.hcmute.shopfee.model;

import com.hcmute.shopfee.enums.errorcode.ShopfeeErrorCode;
import lombok.Data;

@Data
public class ShopfeeException extends RuntimeException{
//    public ShopfeeException(String message) {
//        super(message);
//    }
    private ShopfeeErrorCode subError;
    private ShopfeeErrorCode.SupErrorCode error;
    private String devMessage;
    private int errorCode;
    public ShopfeeException(ShopfeeErrorCode subError, String devMessage) {
        super(subError.supErrorCode().description());
        this.subError = subError;
        this.error = subError.supErrorCode();
        this.devMessage = devMessage;
    }

    public ShopfeeException(ShopfeeErrorCode subError) {
        super(subError.supErrorCode().description());
        this.subError = subError;
        this.error = subError.supErrorCode();
        this.devMessage = null;
    }
    public ShopfeeException(ShopfeeErrorCode.SupErrorCode subError) {
        super(subError.description());
        this.error = subError;
        this.devMessage = null;
    }
    public ShopfeeException(ShopfeeErrorCode.SupErrorCode subError, String devMessage) {
        super(subError.description());
        this.error = subError;
        this.devMessage = devMessage;
    }
//    public ShopfeeException(String errorCode, String devMessage) {
//        super(errorCode);
//        this.devMessage = devMessage;
//    }
//
//
//    public ShopfeeException(String message, String devMessage, int errorCode) {
//        super(message);
//        this.devMessage = devMessage;
//        this.errorCode = errorCode;
//    }
}
