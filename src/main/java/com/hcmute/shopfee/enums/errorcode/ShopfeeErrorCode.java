package com.hcmute.shopfee.enums.errorcode;

import com.hcmute.shopfee.constant.ErrorConstant;

public enum ShopfeeErrorCode {
    USER_NOT_FOUND(1001, ErrorConstant.USER_NOT_FOUND, SupErrorCode.NOT_FOUND),
    USER_TOKEN_NOT_FOUND(1002, ErrorConstant.USER_TOKEN_NOT_FOUND, SupErrorCode.NOT_FOUND),
    EMPLOYEE_TOKEN_NOT_FOUND(1003, ErrorConstant.EMPLOYEE_TOKEN_NOT_FOUND, SupErrorCode.NOT_FOUND),
    EMPLOYEE_NOT_FOUND(1004, ErrorConstant.EMPLOYEE_NOT_FOUND, SupErrorCode.NOT_FOUND),
    ADDRESS_NOT_FOUND(1005, ErrorConstant.ADDRESS_NOT_FOUND, SupErrorCode.NOT_FOUND),
    BANNER_NOT_FOUND(1006, ErrorConstant.BANNER_NOT_FOUND, SupErrorCode.NOT_FOUND),
    BRANCH_NOT_FOUND(1007, ErrorConstant.BRANCH_NOT_FOUND, SupErrorCode.NOT_FOUND),
    CATEGORY_NOT_FOUND(1008, ErrorConstant.CATEGORY_NOT_FOUND, SupErrorCode.NOT_FOUND),
    TRANSACTION_NOT_FOUND(1009, ErrorConstant.TRANSACTION_NOT_FOUND, SupErrorCode.NOT_FOUND),
    COUPON_NOT_FOUND(1010, ErrorConstant.COUPON_NOT_FOUND, SupErrorCode.NOT_FOUND),
    SIZE_NOT_FOUND(1011, ErrorConstant.SIZE_NOT_FOUND, SupErrorCode.NOT_FOUND),
    TOPPING_NOT_FOUND(1012, ErrorConstant.TOPPING_NOT_FOUND, SupErrorCode.NOT_FOUND),
    PRODUCT_NOT_FOUND(1013, ErrorConstant.PRODUCT_NOT_FOUND, SupErrorCode.NOT_FOUND),
    CONFIRMATION_NOT_FOUND(1014, ErrorConstant.CONFIRMATION_NOT_FOUND, SupErrorCode.NOT_FOUND),
    ORDER_BILL_NOT_FOUND(1015, ErrorConstant.ORDER_BILL_NOT_FOUND, SupErrorCode.NOT_FOUND),
    ORDER_REFUND_REQUEST_NOT_FOUND(1016, ErrorConstant.ORDER_REFUND_REQUEST_NOT_FOUND, SupErrorCode.NOT_FOUND),
    FCM_TOKEN_NOT_FOUND(1017, ErrorConstant.FCM_TOKEN_NOT_FOUND, SupErrorCode.NOT_FOUND),
    ROLE_NOT_FOUND(1018, ErrorConstant.ROLE_NOT_FOUND, SupErrorCode.NOT_FOUND),
    ORDER_ITEM_NOT_FOUND(1019, ErrorConstant.ORDER_ITEM_NOT_FOUND, SupErrorCode.NOT_FOUND),
    PRODUCT_REVIEW_NOT_FOUND(1020, ErrorConstant.PRODUCT_REVIEW_NOT_FOUND, SupErrorCode.NOT_FOUND),
    ALBUM_NOT_FOUND(1021, ErrorConstant.ALBUM_NOT_FOUND, SupErrorCode.NOT_FOUND),
    TOKEN_STOLEN(1601, ErrorConstant.TOKEN_STOLEN, SupErrorCode.UNAUTHORIZED),
    WRONG_PASSWORD(1602, ErrorConstant.WRONG_PASSWORD, SupErrorCode.UNAUTHORIZED),
    USER_BLOCKED(1603, ErrorConstant.USER_BLOCKED_STOLEN, SupErrorCode.UNAUTHORIZED),
    CREDENTIAL_WRONG(1604, ErrorConstant.CREDENTIAL_WRONG, SupErrorCode.UNAUTHORIZED),
    IMAGE_INVALID(1301, ErrorConstant.IMAGE_INVALID, SupErrorCode.DATA_SEND_INVALID),
    ADD_MORE_5_ADDRESS(1401, ErrorConstant.ADD_MORE_5_ADDRESS, SupErrorCode.ACTING_INCORRECTLY);
    private final int code;
    private final String description;
    private final SupErrorCode supErrorCode;
    ShopfeeErrorCode(int code, String description, SupErrorCode supErrorCode) {
        this.code = code;
        this.description = description;
        this.supErrorCode = supErrorCode;
    }
    public SupErrorCode supErrorCode() { return this.supErrorCode;}

    public int code() {
        return this.code;
    }
    public String description() {return  this.description;}
    public enum SupErrorCode {
        NOT_FOUND(10, ErrorConstant.NOT_FOUND),
        EXISTED_DATA(11, ErrorConstant.EXISTED_DATA),
        CANT_DELETE(12, ErrorConstant.CANT_DELETE),
        DATA_SEND_INVALID(13, ErrorConstant.DATA_SEND_INVALID),
        ACTING_INCORRECTLY(14, ErrorConstant.ACTING_INCORRECTLY),
        FORBIDDEN(15, ErrorConstant.FORBIDDEN),
        UNAUTHORIZED(16, ErrorConstant.UNAUTHORIZED),
        SERVER_ERROR(17, ErrorConstant.SERVER_ERROR),
        OTHER(18, ErrorConstant.OTHER_ERROR);

        private final int code;

        private final String description;
        SupErrorCode(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int code() {
            return this.code;
        }
        public String description() {return  this.description;}
    }
}
