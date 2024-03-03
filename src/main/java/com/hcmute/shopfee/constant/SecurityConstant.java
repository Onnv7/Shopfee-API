package com.hcmute.shopfee.constant;


import static com.hcmute.shopfee.constant.RouterConstant.*;

public class SecurityConstant {
    public static final String SET_ADMIN_ROLE = "hasRole('ADMIN')";
    public static final String SET_USER_ROLE = "hasRole('USER')";


    // ALL =================================================================
    public static final String[] GET_AUTH_WHITELIST = {
            "/api/payment/**",
            "/IPN/**",
            "/openapi/**", "/v3/api-docs/**", "/openapi/swagger-config/**",
            "/v3/api-docs.yaml", "/swagger-ui/**", "/swagger-ui.html",
            GET_PRODUCT_ALL_VISIBLE_PATH, GET_PRODUCT_BY_CATEGORY_ID_PATH, GET_PRODUCT_ENABLED_BY_ID_PATH, GET_PRODUCT_TOP_QUANTITY_ORDER_PATH,
            GET_CATEGORY_BY_ID_PATH,
            GET_USER_CHECK_EXISTED_PATH, GET_CATEGORY_ALL_WITHOUT_DELETED_PATH, "/tool/**",
            GET_BANNER_VISIBLE_LIST_PATH, GET_COUPON_RELEASE_LIST_PATH,
            GET_COUPON_RELEASE_BY_ID_PATH, GET_PRODUCT_REVIEW_LIST_BY_PRODUCT_ID_PATH


    };
    public static final String[] POST_AUTH_WHITELIST = {
            "/refund",
            "/IPN/**",
            POST_AUTH_EMPLOYEE_LOGIN_PATH, POST_USER_AUTH_REFRESH_TOKEN_PATH, POST_AUTH_REFRESH_EMPLOYEE_TOKEN_PATH,
            POST_AUTH_SEND_OPT_PATH, POST_AUTH_SEND_CODE_TO_REGISTER_PATH, POST_USER_AUTH_RE_SEND_EMAIL_PATH,
            POST_USER_AUTH_REGISTER_PATH, POST_USER_AUTH_LOGIN_PATH, POST_USER_AUTH_VERIFY_EMAIL_PATH, POST_USER_AUTH_SEND_CODE_TO_GET_PWD_PATH,
            POST_COUPON_CHECK_COUPON_LIST_PATH
    };

    public static final String[] PATCH_AUTH_WHITELIST = {
            PATCH_USER_AUTH_CHANGE_PASSWORD_PATH
    };
    // Only USER =================================================================
    public static final String[] POST_USER_PATH = {
            POST_ADDRESS_CREATE_PATH,
            POST_ORDER_CREATE_REVIEW_PATH,
            POST_ORDER_CREATE_ONSITE_PATH, POST_REVIEW_CREATE_PATH,
            POST_REVIEW_INTERACT_PATH
    };
    public static final String[] PATCH_USER_PATH = {
            PATCH_USER_CHANGE_PASSWORD_PATH, PATCH_USER_UPLOAD_AVATAR_PATH
    };
    public static final String[] PUT_USER_PATH = {
            PUT_ADDRESS_UPDATE_PATH,
            PUT_USER_UPDATE_BY_ID_PATH,
    };
    public static final String[] DELETE_USER_PATH = {
            DELETE_ADDRESS_BY_ID_PATH
    };
    public static final String[] GET_USER_PATH = {
            GET_ORDER_ORDERS_BY_USER_ID_AND_ORDER_STATUS_PATH,
            GET_ADDRESS_BY_USER_ID_PATH, GET_ADDRESS_DETAILS_BY_ID_PATH,
            GET_USER_BY_ID_PATH, GET_AUTH_USER_LOGOUT_PATH
    };

    // Only ADMIN =================================================================
    public static final String[] GET_ADMIN_PATH = {
            GET_PRODUCT_DETAILS_BY_ID_PATH,
            GET_USER_ALL_PATH, GET_EMPLOYEE_ALL_PATH,
            GET_CATEGORY_ALL_PATH, GET_PRODUCT_ALL_PATH,
            GET_STATISTICS_REVENUE_BY_TIME_PATH, GET_STATISTICS_REVENUE_CURRENT_DATE_PATH,
            GET_BRANCH_ALL_PATH, GET_BANNER_LIST_PATH, GET_BANNER_DETAILS_BY_ID_PATH, GET_COUPON_LIST_PATH,
            GET_COUPON_SHIPPING_DETAIL_BY_ID_PATH, GET_ORDER_LIST_PATH, GET_COUPON_ORDER_DETAIL_BY_ID_PATH,
            GET_COUPON_PRODUCT_GIFT_DETAIL_BY_ID_PATH, GET_EMPLOYEE_BY_ID_PATH, GET_STATISTICS_QUANTITY_BY_STAGE_PATH,
            GET_COUPON_AMOUNT_OFF_PRODUCT_DETAIL_BY_ID_PATH

    };
    public static final String[] PUT_ADMIN_PATH = {
            PUT_PRODUCT_UPDATE_BY_ID_PATH,
            PUT_CATEGORY_UPDATE_BY_ID_PATH, PUT_BRANCH_UPDATE_PATH,
            PUT_EMPLOYEE_UPDATE_BY_ID_PATH, PUT_BANNER_UPDATE_BY_ID_PATH,
            PUT_COUPON_UPDATE_MONEY_BY_ID_PATH, PUT_COUPON_UPDATE_PRODUCT_GIFT_BY_ID_PATH
    };
    public static final String[] POST_ADMIN_PATH = {
            POST_PRODUCT_CREATE_PATH,
            POST_AUTH_EMPLOYEE_REGISTER_PATH,
            POST_CATEGORY_CREATE_PATH,
            POST_BRANCH_CREATE_PATH, POST_BANNER_CREATE_PATH,
            POST_COUPON_CREATE_SHIPPING_TYPE_PATH, POST_COUPON_CREATE_BUY_GET_TYPE_PATH,
            POST_PRODUCT_CREATE_BEVERAGE_FROM_SUB_PATH, POST_COUPON_CREATE_ORDER_TYPE_PATH,
            POST_COUPON_CREATE_AMOUNT_OFF_PRODUCT_TYPE_PATH, POST_PRODUCT_CREATE_FOOD_FROM_FILE_PATH,
    };
    public static final String[] PATCH_ADMIN_PATH = {
    };

    public static final String[] DELETE_ADMIN_PATH = {
            DELETE_PRODUCT_BY_ID_PATH,
            DELETE_EMPLOYEE_BY_ID_PATH,
            DELETE_CATEGORY_BY_ID_PATH, DELETE_BRANCH_UPDATE_PATH,
            DELETE_SOME_PRODUCT_BY_ID_PATH, DELETE_BANNER_BY_ID_PATH,
            DELETE_COUPON_BY_ID_PATH
    };

    // Only EMPLOYEE =================================================================
    public static final String[] GET_EMPLOYEE_PATH = {
            GET_ORDER_ALL_ORDER_HISTORY_FOR_EMPLOYEE_PATH,
            GET_ORDER_ALL_IN_QUEUE_PATH,
            GET_EMPLOYEE_PROFILE_BY_ID_PATH,
    };

    public static final String[] PATCH_EMPLOYEE_PATH = {
            PATCH_TRANSACTION_UPDATE_COMPLETE_PATH,
            PATCH_EMPLOYEE_UPDATE_PASSWORD_PATH
    };

    // ADMIN + EMPLOYEE =================================================================
    public static final String[] GET_ADMIN_EMPLOYEE_PATH = {
            GET_ORDER_ALL_SHIPPING_PATH,
            GET_ORDER_ORDER_QUANTITY_BY_STATUS_PATH,
            GET_AUTH_EMPLOYEE_LOGOUT_PATH
    };

    public static final String[] PUT_ADMIN_EMPLOYEE_PATH = {
    };
    public static final String[] PATCH_ADMIN_EMPLOYEE_PATH = {
            PATCH_EMPLOYEE_UPDATE_PASSWORD_BY_ID_PATH
    };


    // ADMIN + USER =================================================================
    public static final String[] GET_ADMIN_USER_PATH = {

    };
    public static final String[] PATCH_ADMIN_USER_PATH = {
            PATCH_TRANSACTION_UPDATE_BY_ID_PATH,

    };

    public static final String[] POST_ADMIN_USER_PATH = {
//            TRANSACTION_CREATE_PATH,
//            ORDER_CREATE_PATH,
    };

    public static final String[] PUT_ADMIN_USER_PATH = {
    };


    // EMPLOYEE + USER =================================================================
    public static final String[] GET_EMPLOYEE_USER_PATH = {
            GET_ORDER_STATUS_LINE_PATH
    };

    public static final String[] POST_EMPLOYEE_USER_PATH = {
            POST_ORDER_CREATE_SHIPPING_PATH,
    };
    public static final String[] PATCH_EMPLOYEE_USER_PATH = {
            PATCH_ORDER_UPDATE_STATUS_PATH,
    };
    // ADMIN + EMPLOYEE + USER =================================================================

    public static final String[] GET_ADMIN_EMPLOYEE_USER_PATH = {
            GET_ORDER_DETAILS_BY_ID_PATH,
    };

}
