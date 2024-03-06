package com.hcmute.shopfee.constant;

public class RouterConstant {
    // =================================================
    public static final String USER_BASE_PATH = "/api/user";
    public static final String ADDRESS_BASE_PATH = "/api/address";
    public static final String REVIEW_BASE_PATH = "/api/review";
    public static final String BRANCH_BASE_PATH = "/api/branch";
    public static final String EMPLOYEE_AUTH_BASE_PATH = "/api/auth/employee";
    public static final String USER_AUTH_BASE_PATH = "/api/auth/user";
    public static final String BANNER_BASE_PATH = "/api/banner";
    public static final String CATEGORY_BASE_PATH = "/api/category";
    public static final String COUPON_BASE_PATH = "/api/coupon";
    public static final String PRODUCT_BASE_PATH = "/api/product";
    public static final String EMPLOYEE_BASE_PATH = "/api/employee";
    public static final String ORDER_BASE_PATH = "/api/order";
    public static final String TRANSACTION_BASE_PATH = "/api/transaction";
    public static final String STATISTICS_BASE_PATH = "/api/statistics";
    // ALL PATH=================================================
    public static final String USER_ALL_PATH = USER_BASE_PATH + "/**";
    public static final String AUTH_ALL_PATH = EMPLOYEE_AUTH_BASE_PATH + "/**";
    public static final String CATEGORY_ALL_PATH = CATEGORY_BASE_PATH + "/**";
    public static final String PRODUCT_ALL_PATH = PRODUCT_BASE_PATH + "/**";
    public static final String ORDER_ALL_PATH = PRODUCT_BASE_PATH + "/**";
    // ID PATH =================================================================
    public static final String COUPON_ID_PATH = "/{couponId}";
    public static final String COUPON_ID = "couponId";

    public static final String CATEGORY_ID_PATH = "/{categoryId}";
    public static final String CATEGORY_ID = "categoryId";
    public static final String PRODUCT_ID_PATH = "/{productId}";
    public static final String PRODUCT_ID = "productId";
    public static final String BANNER_ID_PATH = "/{bannerId}";
    public static final String BANNER_ID = "bannerId";
    public static final String USER_ID_PATH = "/{userId}";
    public static final String USER_ID = "userId";
    public static final String PRODUCT_REVIEW_ID_PATH = "/{productReviewId}";
    public static final String PRODUCT_REVIEW_ID = "productReviewId";
    public static final String ADDRESS_ID_PATH = "/{addressId}";
    public static final String ADDRESS_ID = "addressId";
    public static final String EMPLOYEE_ID_PATH = "/{employeeId}";
    public static final String EMPLOYEE_ID = "employeeId";
    public static final String ORDER_ID_PATH = "/{orderId}";
    public static final String ORDER_ID = "orderId";
    public static final String TRANSACTION_ID_PATH = "/{transId}";
    public static final String TRANSACTION_ID = "transId";
    public static final String BRANCH_ID_PATH = "/{branchId}";
    public static final String BRANCH_ID = "branchId";

    // ENDPOINT URL USER =================================================================
    public static final String PUT_USER_UPDATE_BY_ID_SUB_PATH = USER_ID_PATH;
    public static final String PUT_USER_UPDATE_BY_ID_PATH = USER_BASE_PATH + PUT_USER_UPDATE_BY_ID_SUB_PATH;
    public static final String PATCH_USER_CHANGE_PASSWORD_SUB_PATH = USER_ID_PATH + "/change-password";
    public static final String PATCH_USER_CHANGE_PASSWORD_PATH = USER_BASE_PATH + PATCH_USER_CHANGE_PASSWORD_SUB_PATH;
    public static final String GET_USER_BY_ID_SUB_PATH = USER_ID_PATH + "/view";
    public static final String GET_USER_BY_ID_PATH = USER_BASE_PATH + GET_USER_BY_ID_SUB_PATH;
    public static final String GET_USER_ALL_SUB_PATH = "";
    public static final String GET_USER_ALL_PATH = USER_BASE_PATH + GET_USER_ALL_SUB_PATH;
    public static final String GET_USER_CHECK_EXISTED_SUB_PATH = "/registered";
    public static final String GET_USER_CHECK_EXISTED_PATH = USER_BASE_PATH + GET_USER_CHECK_EXISTED_SUB_PATH;
    public static final String PATCH_USER_UPLOAD_AVATAR_SUB_PATH = USER_ID_PATH + "/upload/avatar";
    public static final String PATCH_USER_UPLOAD_AVATAR_PATH = USER_BASE_PATH + PATCH_USER_UPLOAD_AVATAR_SUB_PATH;

    // ENDPOINT URL ADDRESS =================================================================
    public static final String POST_ADDRESS_CREATE_SUB_PATH = "/user" + USER_ID_PATH;
    public static final String POST_ADDRESS_CREATE_PATH = ADDRESS_BASE_PATH + POST_ADDRESS_CREATE_SUB_PATH;
    public static final String PUT_ADDRESS_UPDATE_SUB_PATH = ADDRESS_ID_PATH;
    public static final String PUT_ADDRESS_UPDATE_PATH = ADDRESS_BASE_PATH + PUT_ADDRESS_UPDATE_SUB_PATH;
    public static final String DELETE_ADDRESS_BY_ID_SUB_PATH = ADDRESS_ID_PATH;
    public static final String DELETE_ADDRESS_BY_ID_PATH = ADDRESS_BASE_PATH + DELETE_ADDRESS_BY_ID_SUB_PATH;
    public static final String GET_ADDRESS_BY_USER_ID_SUB_PATH = "/user" + USER_ID_PATH;
    public static final String GET_ADDRESS_BY_USER_ID_PATH = ADDRESS_BASE_PATH + GET_ADDRESS_BY_USER_ID_SUB_PATH;
    public static final String GET_ADDRESS_DETAILS_BY_ID_SUB_PATH = ADDRESS_ID_PATH;
    public static final String GET_ADDRESS_DETAILS_BY_ID_PATH = ADDRESS_BASE_PATH + GET_ADDRESS_DETAILS_BY_ID_SUB_PATH;

    // ENDPOINT URL ADDRESS =================================================================
    public static final String POST_REVIEW_CREATE_SUB_PATH = "";
    public static final String POST_REVIEW_CREATE_PATH = REVIEW_BASE_PATH + POST_REVIEW_CREATE_SUB_PATH;
    public static final String POST_REVIEW_INTERACT_SUB_PATH = "/product-review" + PRODUCT_REVIEW_ID_PATH;
    public static final String POST_REVIEW_INTERACT_PATH = REVIEW_BASE_PATH + POST_REVIEW_INTERACT_SUB_PATH;
    public static final String GET_PRODUCT_REVIEW_LIST_BY_PRODUCT_ID_SUB_PATH = "/product" + PRODUCT_ID_PATH;
    public static final String GET_PRODUCT_REVIEW_LIST_BY_PRODUCT_ID_PATH = REVIEW_BASE_PATH + GET_PRODUCT_REVIEW_LIST_BY_PRODUCT_ID_SUB_PATH;

    // ENDPOINT URL STATISTICS =================================================================
    public static final String GET_STATISTICS_REVENUE_CURRENT_DATE_SUB_PATH = "/revenue/today";
    public static final String GET_STATISTICS_REVENUE_CURRENT_DATE_PATH = STATISTICS_BASE_PATH + GET_STATISTICS_REVENUE_CURRENT_DATE_SUB_PATH;
    public static final String GET_STATISTICS_QUANTITY_BY_STAGE_SUB_PATH = "/order-quantity";
    public static final String GET_STATISTICS_QUANTITY_BY_STAGE_PATH = STATISTICS_BASE_PATH + GET_STATISTICS_QUANTITY_BY_STAGE_SUB_PATH;
    public static final String GET_STATISTICS_REVENUE_BY_TIME_SUB_PATH = "/chart/revenue";
    public static final String GET_STATISTICS_REVENUE_BY_TIME_PATH = STATISTICS_BASE_PATH + GET_STATISTICS_REVENUE_BY_TIME_SUB_PATH;

    // ENDPOINT URL PRODUCT =================================================================
    public static final String GET_PRODUCT_DETAILS_BY_ID_SUB_PATH = PRODUCT_ID_PATH + "/details";
    public static final String GET_PRODUCT_DETAILS_BY_ID_PATH = PRODUCT_BASE_PATH + GET_PRODUCT_DETAILS_BY_ID_SUB_PATH;
    public static final String GET_PRODUCT_VIEW_BY_ID_SUB_PATH = PRODUCT_ID_PATH + "/view";
    public static final String GET_PRODUCT_ENABLED_BY_ID_PATH = PRODUCT_BASE_PATH + GET_PRODUCT_VIEW_BY_ID_SUB_PATH;
    public static final String GET_PRODUCT_BY_CATEGORY_ID_SUB_PATH = "/category" + CATEGORY_ID_PATH;
    public static final String GET_PRODUCT_BY_CATEGORY_ID_PATH = PRODUCT_BASE_PATH + GET_PRODUCT_BY_CATEGORY_ID_SUB_PATH;
    public static final String GET_PRODUCT_ALL_VISIBLE_SUB_PATH = "/visible";
    public static final String GET_PRODUCT_ALL_VISIBLE_PATH = PRODUCT_BASE_PATH + GET_PRODUCT_ALL_VISIBLE_SUB_PATH;
    public static final String GET_PRODUCT_ALL_SUB_PATH = "";
    public static final String GET_PRODUCT_ALL_PATH = PRODUCT_BASE_PATH + GET_PRODUCT_ALL_SUB_PATH;
    public static final String PUT_PRODUCT_UPDATE_BY_ID_SUB_PATH = PRODUCT_ID_PATH;
    public static final String PUT_PRODUCT_UPDATE_BY_ID_PATH = PRODUCT_BASE_PATH + PUT_PRODUCT_UPDATE_BY_ID_SUB_PATH;
    public static final String DELETE_PRODUCT_BY_ID_SUB_PATH = PRODUCT_ID_PATH;
    public static final String DELETE_PRODUCT_BY_ID_PATH = PRODUCT_BASE_PATH + DELETE_PRODUCT_BY_ID_SUB_PATH;
    public static final String DELETE_SOME_PRODUCT_BY_ID_SUB_PATH = "";
    public static final String DELETE_SOME_PRODUCT_BY_ID_PATH = PRODUCT_BASE_PATH + DELETE_SOME_PRODUCT_BY_ID_SUB_PATH;
    public static final String POST_PRODUCT_CREATE_SUB_PATH = "";
    public static final String POST_PRODUCT_CREATE_PATH = PRODUCT_BASE_PATH + POST_PRODUCT_CREATE_SUB_PATH;
    public static final String GET_PRODUCT_TOP_RATED_PRODUCTS_SUB_PATH = "/top-rating/{quantity}";
    public static final String GET_PRODUCT_TOP_QUANTITY_ORDER_PATH = PRODUCT_BASE_PATH + GET_PRODUCT_TOP_RATED_PRODUCTS_SUB_PATH;
    public static final String GET_PRODUCT_TOP_SELLING_PRODUCTS_SUB_PATH = "/top-selling/{quantity}";
    public static final String GET_PRODUCT_TOP_SELLING_PRODUCTS_PATH = PRODUCT_BASE_PATH + GET_PRODUCT_TOP_SELLING_PRODUCTS_SUB_PATH;
    public static final String POST_PRODUCT_CREATE_BEVERAGE_FROM_FILE_SUB_PATH = "/beverage/import";
    public static final String POST_PRODUCT_CREATE_BEVERAGE_FROM_SUB_PATH = PRODUCT_BASE_PATH + POST_PRODUCT_CREATE_BEVERAGE_FROM_FILE_SUB_PATH;
    public static final String POST_PRODUCT_CREATE_FOOD_FROM_FILE_SUB_PATH = "/food/import";
    public static final String POST_PRODUCT_CREATE_FOOD_FROM_FILE_PATH = PRODUCT_BASE_PATH + POST_PRODUCT_CREATE_FOOD_FROM_FILE_SUB_PATH;


    // ENDPOINT URL EMPLOYEE =================================================================
    public static final String GET_EMPLOYEE_BY_ID_SUB_PATH = EMPLOYEE_ID_PATH;
    public static final String GET_EMPLOYEE_BY_ID_PATH = EMPLOYEE_BASE_PATH + GET_EMPLOYEE_BY_ID_SUB_PATH;

    public static final String GET_EMPLOYEE_PROFILE_BY_ID_SUB_PATH = EMPLOYEE_ID_PATH + "/profile";
    public static final String GET_EMPLOYEE_PROFILE_BY_ID_PATH = EMPLOYEE_BASE_PATH + GET_EMPLOYEE_PROFILE_BY_ID_SUB_PATH;
    public static final String GET_EMPLOYEE_ALL_SUB_PATH = "";
    public static final String GET_EMPLOYEE_ALL_PATH = EMPLOYEE_BASE_PATH + GET_EMPLOYEE_ALL_SUB_PATH;
    public static final String PUT_EMPLOYEE_UPDATE_BY_ID_SUB_PATH = EMPLOYEE_ID_PATH;
    public static final String PUT_EMPLOYEE_UPDATE_BY_ID_PATH = EMPLOYEE_BASE_PATH + PUT_EMPLOYEE_UPDATE_BY_ID_SUB_PATH;
    public static final String PATCH_EMPLOYEE_UPDATE_PASSWORD_BY_ID_SUB_PATH = EMPLOYEE_ID_PATH + "/update-password";
    public static final String PATCH_EMPLOYEE_UPDATE_PASSWORD_BY_ID_PATH = EMPLOYEE_BASE_PATH + PATCH_EMPLOYEE_UPDATE_PASSWORD_BY_ID_SUB_PATH;
    public static final String DELETE_EMPLOYEE_BY_ID_SUB_PATH = EMPLOYEE_ID_PATH;
    public static final String DELETE_EMPLOYEE_BY_ID_PATH = EMPLOYEE_BASE_PATH + DELETE_EMPLOYEE_BY_ID_SUB_PATH;
    public static final String PATCH_EMPLOYEE_UPDATE_PASSWORD_SUB_PATH = EMPLOYEE_ID_PATH + "/change-password";
    public static final String PATCH_EMPLOYEE_UPDATE_PASSWORD_PATH = EMPLOYEE_BASE_PATH + PATCH_EMPLOYEE_UPDATE_PASSWORD_SUB_PATH;

    // ENDPOINT URL CATEGORY =================================================================

    public static final String GET_CATEGORY_BY_SUB_ID_PATH = CATEGORY_ID_PATH + "/details";
    public static final String GET_CATEGORY_BY_ID_PATH = CATEGORY_BASE_PATH + GET_CATEGORY_BY_SUB_ID_PATH;
    public static final String GET_CATEGORY_ALL_SUB_PATH = "";
    public static final String GET_CATEGORY_ALL_PATH = CATEGORY_BASE_PATH + GET_CATEGORY_ALL_SUB_PATH;
    public static final String GET_CATEGORY_ALL_WITHOUT_DELETED_SUB_PATH = "/visible";
    public static final String GET_CATEGORY_ALL_WITHOUT_DELETED_PATH = CATEGORY_BASE_PATH + GET_CATEGORY_ALL_WITHOUT_DELETED_SUB_PATH;
    public static final String PUT_CATEGORY_UPDATE_BY_ID_SUB_PATH = CATEGORY_ID_PATH;
    public static final String PUT_CATEGORY_UPDATE_BY_ID_PATH = CATEGORY_BASE_PATH + PUT_CATEGORY_UPDATE_BY_ID_SUB_PATH;
    public static final String DELETE_CATEGORY_BY_ID_SUB_PATH = CATEGORY_ID_PATH;
    public static final String DELETE_CATEGORY_BY_ID_PATH = CATEGORY_BASE_PATH + DELETE_CATEGORY_BY_ID_SUB_PATH;
    public static final String POST_CATEGORY_CREATE_SUB_PATH = "";
    public static final String POST_CATEGORY_CREATE_PATH = CATEGORY_BASE_PATH + POST_CATEGORY_CREATE_SUB_PATH;

    // ENDPOINT URL ORDER =================================================================
    public static final String POST_ORDER_CREATE_SHIPPING_SUB_PATH = "/shipping";
    public static final String POST_ORDER_CREATE_SHIPPING_PATH = ORDER_BASE_PATH + POST_ORDER_CREATE_SHIPPING_SUB_PATH;
    public static final String POST_ORDER_CREATE_ONSITE_SUB_PATH = "/onsite";
    public static final String POST_ORDER_CREATE_ONSITE_PATH = ORDER_BASE_PATH + POST_ORDER_CREATE_ONSITE_SUB_PATH;
    public static final String PATCH_ORDER_UPDATE_STATUS_SUB_PATH = ORDER_ID_PATH + "/by/{maker}";
    public static final String PATCH_ORDER_UPDATE_STATUS_PATH = ORDER_BASE_PATH + PATCH_ORDER_UPDATE_STATUS_SUB_PATH;
    public static final String GET_ORDER_ALL_SHIPPING_SUB_PATH = "/shipping";
    public static final String GET_ORDER_ALL_SHIPPING_PATH = ORDER_BASE_PATH + GET_ORDER_ALL_SHIPPING_SUB_PATH;
    public static final String GET_ORDER_ALL_IN_QUEUE_SUB_PATH = "/queue";
    public static final String GET_ORDER_ALL_IN_QUEUE_PATH = ORDER_BASE_PATH + GET_ORDER_ALL_IN_QUEUE_SUB_PATH;
    public static final String GET_ORDER_LIST_SUB_PATH = "";
    public static final String GET_ORDER_LIST_PATH = ORDER_BASE_PATH + GET_ORDER_LIST_SUB_PATH;
    public static final String GET_ORDER_DETAILS_BY_ID_SUB_PATH = ORDER_ID_PATH + "/details";
    public static final String GET_ORDER_DETAILS_BY_ID_PATH = ORDER_BASE_PATH + GET_ORDER_DETAILS_BY_ID_SUB_PATH;
    public static final String GET_ORDER_ORDERS_BY_USER_ID_AND_ORDER_STATUS_SUB_PATH = "/history/user" + USER_ID_PATH;
    public static final String GET_ORDER_ORDERS_BY_USER_ID_AND_ORDER_STATUS_PATH = ORDER_BASE_PATH + GET_ORDER_ORDERS_BY_USER_ID_AND_ORDER_STATUS_SUB_PATH;
    public static final String POST_ORDER_CREATE_REVIEW_SUB_PATH = "/rating" + ORDER_ID_PATH;
    public static final String POST_ORDER_CREATE_REVIEW_PATH = ORDER_BASE_PATH + POST_ORDER_CREATE_REVIEW_SUB_PATH;
    public static final String GET_ORDER_STATUS_LINE_SUB_PATH = ORDER_ID_PATH + "/status-line";
    public static final String GET_ORDER_STATUS_LINE_PATH = ORDER_BASE_PATH + GET_ORDER_STATUS_LINE_SUB_PATH;
    public static final String GET_ORDER_ALL_ORDER_HISTORY_FOR_EMPLOYEE_SUB_PATH = "/history/{orderStatus}";
    public static final String GET_ORDER_ALL_ORDER_HISTORY_FOR_EMPLOYEE_PATH = ORDER_BASE_PATH + GET_ORDER_ALL_ORDER_HISTORY_FOR_EMPLOYEE_SUB_PATH;
    public static final String GET_ORDER_ORDER_QUANTITY_BY_STATUS_SUB_PATH = "/quantity/today";
    public static final String GET_ORDER_ORDER_QUANTITY_BY_STATUS_PATH = ORDER_BASE_PATH + GET_ORDER_ORDER_QUANTITY_BY_STATUS_SUB_PATH;

    // ENDPOINT URL TRANSACTION =================================================================
    public static final String PATCH_TRANSACTION_UPDATE_BY_ID_SUB_PATH = TRANSACTION_ID_PATH;
    public static final String PATCH_TRANSACTION_UPDATE_BY_ID_PATH = TRANSACTION_BASE_PATH + PATCH_TRANSACTION_UPDATE_BY_ID_SUB_PATH;
    public static final String PATCH_TRANSACTION_UPDATE_COMPLETE_SUB_PATH = TRANSACTION_ID_PATH + "/complete";
    public static final String PATCH_TRANSACTION_UPDATE_COMPLETE_PATH = TRANSACTION_BASE_PATH + PATCH_TRANSACTION_UPDATE_COMPLETE_SUB_PATH;


    // ENDPOINT URL AUTH =================================================================
    public static final String POST_AUTH_SEND_OPT_SUB_PATH = "/send-opt";
    public static final String POST_AUTH_SEND_OPT_PATH = EMPLOYEE_AUTH_BASE_PATH + POST_AUTH_SEND_OPT_SUB_PATH;
    public static final String POST_AUTH_SEND_CODE_TO_REGISTER_SUB_PATH = "/register/send-code";
    public static final String POST_AUTH_SEND_CODE_TO_REGISTER_PATH = EMPLOYEE_AUTH_BASE_PATH + POST_AUTH_SEND_CODE_TO_REGISTER_SUB_PATH;
    public static final String POST_AUTH_EMPLOYEE_LOGIN_SUB_PATH = "/login";
    public static final String POST_AUTH_EMPLOYEE_LOGIN_PATH = EMPLOYEE_AUTH_BASE_PATH + POST_AUTH_EMPLOYEE_LOGIN_SUB_PATH;
    public static final String GET_AUTH_EMPLOYEE_LOGOUT_SUB_PATH = "/logout";
    public static final String GET_AUTH_EMPLOYEE_LOGOUT_PATH = EMPLOYEE_AUTH_BASE_PATH + GET_AUTH_EMPLOYEE_LOGOUT_SUB_PATH;
    public static final String POST_AUTH_REFRESH_EMPLOYEE_TOKEN_SUB_PATH = "/refresh-token";
    public static final String POST_AUTH_REFRESH_EMPLOYEE_TOKEN_PATH = EMPLOYEE_AUTH_BASE_PATH + POST_AUTH_REFRESH_EMPLOYEE_TOKEN_SUB_PATH;
    public static final String POST_AUTH_EMPLOYEE_REGISTER_SUB_PATH = "";
    public static final String POST_AUTH_EMPLOYEE_REGISTER_PATH = EMPLOYEE_AUTH_BASE_PATH + POST_AUTH_EMPLOYEE_REGISTER_SUB_PATH;
    // ENDPOINT URL USER AUTH =================================================================
    public static final String POST_USER_AUTH_REGISTER_SUB_PATH = "/register";
    public static final String POST_USER_AUTH_REGISTER_PATH = USER_AUTH_BASE_PATH + POST_USER_AUTH_REGISTER_SUB_PATH;
    public static final String POST_USER_AUTH_FIREBASE_REGISTER_SUB_PATH = "/firebase/register";
    public static final String POST_USER_AUTH_FIREBASE_REGISTER_PATH = USER_AUTH_BASE_PATH + POST_USER_AUTH_FIREBASE_REGISTER_SUB_PATH;
    public static final String POST_USER_AUTH_LOGIN_SUB_PATH = "/login";
    public static final String POST_USER_AUTH_LOGIN_PATH = USER_AUTH_BASE_PATH + POST_USER_AUTH_LOGIN_SUB_PATH;
    public static final String POST_USER_AUTH_FIREBASE_LOGIN_SUB_PATH = "/firebase/login";
    public static final String POST_USER_AUTH_FIREBASE_LOGIN_PATH = USER_AUTH_BASE_PATH + POST_USER_AUTH_FIREBASE_LOGIN_SUB_PATH;
    public static final String GET_AUTH_USER_LOGOUT_SUB_PATH = "/logout";
    public static final String GET_AUTH_USER_LOGOUT_PATH = USER_AUTH_BASE_PATH + GET_AUTH_USER_LOGOUT_SUB_PATH;
    public static final String POST_USER_AUTH_RE_SEND_EMAIL_SUB_PATH = "/resend-email";
    public static final String POST_USER_AUTH_RE_SEND_EMAIL_PATH = USER_AUTH_BASE_PATH + POST_USER_AUTH_RE_SEND_EMAIL_SUB_PATH;
    public static final String POST_USER_AUTH_SEND_CODE_TO_GET_PWD_SUB_PATH = "/password/send-code";
    public static final String POST_USER_AUTH_SEND_CODE_TO_GET_PWD_PATH = USER_AUTH_BASE_PATH + POST_USER_AUTH_SEND_CODE_TO_GET_PWD_SUB_PATH;
    public static final String POST_USER_AUTH_VERIFY_EMAIL_SUB_PATH = "/verify";
    public static final String POST_USER_AUTH_VERIFY_EMAIL_PATH = USER_AUTH_BASE_PATH + POST_USER_AUTH_VERIFY_EMAIL_SUB_PATH;
    public static final String PATCH_USER_AUTH_CHANGE_PASSWORD_SUB_PATH = "/change-password";
    public static final String PATCH_USER_AUTH_CHANGE_PASSWORD_PATH = USER_AUTH_BASE_PATH + PATCH_USER_AUTH_CHANGE_PASSWORD_SUB_PATH;
    public static final String POST_USER_AUTH_REFRESH_TOKEN_SUB_PATH = "/refresh-token";
    public static final String POST_USER_AUTH_REFRESH_TOKEN_PATH = USER_AUTH_BASE_PATH + POST_USER_AUTH_REFRESH_TOKEN_SUB_PATH;

    // ENDPOINT URL BRANCH =================================================================
    public static final String POST_BRANCH_CREATE_SUB_PATH = "";
    public static final String POST_BRANCH_CREATE_PATH = BRANCH_BASE_PATH + POST_BRANCH_CREATE_SUB_PATH;
    public static final String PUT_BRANCH_UPDATE_SUB_PATH = BRANCH_ID_PATH;
    public static final String PUT_BRANCH_UPDATE_PATH = BRANCH_BASE_PATH + PUT_BRANCH_UPDATE_SUB_PATH;
    public static final String DELETE_BRANCH_UPDATE_SUB_PATH = BRANCH_ID_PATH;
    public static final String DELETE_BRANCH_UPDATE_PATH = BRANCH_BASE_PATH + DELETE_BRANCH_UPDATE_SUB_PATH;
    public static final String GET_BRANCH_ALL_SUB_PATH = "";
    public static final String GET_BRANCH_ALL_PATH = BRANCH_BASE_PATH + GET_BRANCH_ALL_SUB_PATH;

    // ENDPOINT URL BANNER =================================================================
    public static final String POST_BANNER_CREATE_SUB_PATH = "";
    public static final String POST_BANNER_CREATE_PATH = BANNER_BASE_PATH + POST_BANNER_CREATE_SUB_PATH;
    public static final String PUT_BANNER_UPDATE_BY_ID_SUB_PATH = BANNER_ID_PATH;
    public static final String PUT_BANNER_UPDATE_BY_ID_PATH = BANNER_BASE_PATH + PUT_BANNER_UPDATE_BY_ID_SUB_PATH;
    public static final String DELETE_BANNER_BY_ID_SUB_PATH = BANNER_ID_PATH;
    public static final String DELETE_BANNER_BY_ID_PATH = BANNER_BASE_PATH + DELETE_BANNER_BY_ID_SUB_PATH;
    public static final String GET_BANNER_LIST_SUB_PATH = "";
    public static final String GET_BANNER_LIST_PATH = BANNER_BASE_PATH + GET_BANNER_LIST_SUB_PATH;
    public static final String GET_BANNER_VISIBLE_LIST_SUB_PATH = "/visible";
    public static final String GET_BANNER_VISIBLE_LIST_PATH = BANNER_BASE_PATH + GET_BANNER_VISIBLE_LIST_SUB_PATH;
    public static final String GET_BANNER_DETAILS_BY_ID_SUB_PATH = BANNER_ID_PATH + "/details";
    public static final String GET_BANNER_DETAILS_BY_ID_PATH = BANNER_BASE_PATH + GET_BANNER_DETAILS_BY_ID_SUB_PATH;

    // ENDPOINT URL COUPON =================================================================
    public static final String POST_COUPON_CREATE_SHIPPING_TYPE_SUB_PATH = "/shipping";
    public static final String POST_COUPON_CREATE_SHIPPING_TYPE_PATH = COUPON_BASE_PATH + POST_COUPON_CREATE_SHIPPING_TYPE_SUB_PATH;
    public static final String POST_COUPON_CREATE_ORDER_TYPE_SUB_PATH = "/order";
    public static final String POST_COUPON_CREATE_ORDER_TYPE_PATH = COUPON_BASE_PATH + POST_COUPON_CREATE_ORDER_TYPE_SUB_PATH;
    public static final String POST_COUPON_CREATE_AMOUNT_OFF_PRODUCT_TYPE_SUB_PATH = "/amount-off-product";
    public static final String POST_COUPON_CREATE_AMOUNT_OFF_PRODUCT_TYPE_PATH = COUPON_BASE_PATH + POST_COUPON_CREATE_AMOUNT_OFF_PRODUCT_TYPE_SUB_PATH;
    public static final String POST_COUPON_CREATE_BUY_GET_TYPE_SUB_PATH = "/product-gift";
    public static final String POST_COUPON_CREATE_BUY_GET_TYPE_PATH = COUPON_BASE_PATH + POST_COUPON_CREATE_BUY_GET_TYPE_SUB_PATH;
    public static final String PUT_COUPON_UPDATE_MONEY_BY_ID_SUB_PATH = COUPON_ID_PATH + "/{couponType}";
    public static final String PUT_COUPON_UPDATE_MONEY_BY_ID_PATH = COUPON_BASE_PATH + PUT_COUPON_UPDATE_MONEY_BY_ID_SUB_PATH;
    public static final String PUT_COUPON_UPDATE_PRODUCT_GIFT_BY_ID_SUB_PATH = COUPON_ID_PATH + "/product-gift";
    public static final String PUT_COUPON_UPDATE_PRODUCT_GIFT_BY_ID_PATH = COUPON_BASE_PATH + PUT_COUPON_UPDATE_PRODUCT_GIFT_BY_ID_SUB_PATH;
    public static final String DELETE_COUPON_BY_ID_SUB_PATH = COUPON_ID_PATH;
    public static final String DELETE_COUPON_BY_ID_PATH = COUPON_BASE_PATH + DELETE_COUPON_BY_ID_SUB_PATH;
    public static final String GET_COUPON_RELEASE_LIST_SUB_PATH = "/status/release";
    public static final String GET_COUPON_RELEASE_LIST_PATH = COUPON_BASE_PATH + GET_COUPON_RELEASE_LIST_SUB_PATH;
    public static final String GET_COUPON_RELEASE_BY_ID_SUB_PATH = COUPON_ID_PATH + "/release";
    public static final String GET_COUPON_RELEASE_BY_ID_PATH = COUPON_BASE_PATH + GET_COUPON_RELEASE_BY_ID_SUB_PATH;
    public static final String GET_COUPON_LIST_SUB_PATH = "";
    public static final String GET_COUPON_LIST_PATH = COUPON_BASE_PATH + GET_COUPON_LIST_SUB_PATH;
    public static final String GET_COUPON_SHIPPING_DETAIL_BY_ID_SUB_PATH = COUPON_ID_PATH + "/shipping";
    public static final String GET_COUPON_SHIPPING_DETAIL_BY_ID_PATH = COUPON_BASE_PATH + GET_COUPON_SHIPPING_DETAIL_BY_ID_SUB_PATH;
    public static final String GET_COUPON_ORDER_DETAIL_BY_ID_SUB_PATH = COUPON_ID_PATH + "/order";
    public static final String GET_COUPON_ORDER_DETAIL_BY_ID_PATH = COUPON_BASE_PATH + GET_COUPON_ORDER_DETAIL_BY_ID_SUB_PATH;
    public static final String GET_COUPON_PRODUCT_GIFT_DETAIL_BY_ID_SUB_PATH = COUPON_ID_PATH + "/product-gift";
    public static final String GET_COUPON_PRODUCT_GIFT_DETAIL_BY_ID_PATH = COUPON_BASE_PATH + GET_COUPON_PRODUCT_GIFT_DETAIL_BY_ID_SUB_PATH;
    public static final String GET_COUPON_AMOUNT_OFF_PRODUCT_DETAIL_BY_ID_SUB_PATH = COUPON_ID_PATH + "/amount-off-product";
    public static final String GET_COUPON_AMOUNT_OFF_PRODUCT_DETAIL_BY_ID_PATH = COUPON_BASE_PATH + GET_COUPON_AMOUNT_OFF_PRODUCT_DETAIL_BY_ID_SUB_PATH;
    public static final String POST_COUPON_CHECK_COUPON_LIST_SUB_PATH = "/check-coupon";
    public static final String POST_COUPON_CHECK_COUPON_LIST_PATH = COUPON_BASE_PATH + POST_COUPON_CHECK_COUPON_LIST_SUB_PATH;

}
