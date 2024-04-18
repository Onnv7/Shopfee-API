package com.hcmute.shopfee.constant;

public class ErrorConstant {
    public static final String NOT_FOUND = "Not found";
    public static final String EXISTED_DATA = "Data already exists";
    public static final String CANT_DELETE = "Cannot be deleted because this data is related to other data";
    public static final String DATA_SEND_INVALID = "Data submitted is invalid";
    public static final String ACTING_INCORRECTLY = "Acting incorrectly according to established procedures";
    public static final String FORBIDDEN = "Access is denied";
    public static final String UNAUTHORIZED = "Client is not authenticated";
    public static final String SERVER_ERROR = "Server error";

    // message details =================================================
    public static final String COUPON_INVALID = "Coupon is invalid";
    public static final String IMAGE_INVALID = "Invalid image";
    public static final String USER_ID_NOT_FOUND = "User with id ";
    public static final String USER_TOKEN_NOT_FOUND = "User's token with user's id ";
    public static final String EMPLOYEE_TOKEN_NOT_FOUND = "Employee's token with employee's id ";
    public static final String EMPLOYEE_ID_NOT_FOUND = "Employee with id ";
    public static final String USER_EMAIL_NOT_FOUND = "User with username ";
    public static final String ADDRESS_ID_NOT_FOUND = "Address with id ";
    public static final String BANNER_ID_NOT_FOUND = "Banner with id ";
    public static final String BRANCH_ID_NOT_FOUND = "Branch with id ";
    public static final String CATEGORY_ID_NOT_FOUND = "Category with id ";
    public static final String TRANSACTION_ID_NOT_FOUND = "Transaction with id ";
    public static final String COUPON_ID_NOT_FOUND = "Coupon with id ";
    public static final String COUPON_CODE_NOT_FOUND = "Coupon with code ";
    public static final String PRODUCT_ID_NOT_FOUND = "Product with id ";
    public static final String ORDER_BILL_ID_NOT_FOUND = "Order bill with id ";
    public static final String ORDER_RETURN_ID_NOT_FOUND = "Order return request with id ";
    public static final String FCM_TOKEN_ID_NOT_FOUND = "Fcm token with id ";
    public static final String ORDER_ITEM_ID_NOT_FOUND = "Order item with id ";
    public static final String PRODUCT_REVIEW_ID_NOT_FOUND = "Product review with id ";
    public static final String TOKEN_STOLEN = "Tokens have been stolen";
    public static final String WRONG_PASSWORD = "Password is wrong";
    public static final String VNPAY_MONEY_INVALID = "Invalid payment amount";
    public static final String ALBUM_ID_INVALID = "Image with id ";


    public static int getErrorCode(String errorString) {
        switch (errorString) {
            case NOT_FOUND:
                return 104;
            case EXISTED_DATA:
                return 105;
            case CANT_DELETE:
                return 107;
            case DATA_SEND_INVALID:
                return 108;
            case ACTING_INCORRECTLY:
                return 109;
            case FORBIDDEN:
                return 110;
            case UNAUTHORIZED:
                return 111;
            case SERVER_ERROR:
                return 112;
            default:
                return 115;
        }
    }
}
