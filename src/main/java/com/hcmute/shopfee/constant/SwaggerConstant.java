package com.hcmute.shopfee.constant;

public class SwaggerConstant {
    public static final int TOPPING_PRICE_MIN = 5000;
    public static final int PRODUCT_PRICE_MIN = 5000;
    public static final int PASSWORD_LENGTH_MIN = 6;
    public static final int PASSWORD_LENGTH_MAX = 32;
    public static final int ORDER_QUANTITY_MIN = 1;
    public static final String PHONE_NUMBER_REGEX = "^\\d{10}$";
    private static final String NOTIFICATION_NOT_DATA = "This endpoint returns message and no data";

    public static final String JSON_MEDIA_TYPE = "application/json";
    public static final String FORM_DATA_MEDIA_TYPE = "multipart/form-data";

    // For authentication =================================================================
    public static final String AUTH_CONTROLLER_TITLE = "EMPLOYEE AUTH MANAGEMENT";
    public static final String AUTH_REGISTER_SUM = "Register new user account with some information";
    public static final String AUTH_SEND_OTP_TO_PHONE_NUMBER_SUM = "Send OTP code to phone number (The function is under maintenance)";
    public static final String AUTH_EMPLOYEE_LOGIN_SUM = "Employee login to get token";
    public static final String AUTH_REFRESH_EMPLOYEE_TOKEN_SUM = "Create new access token and refresh token for employee";
    public static final String AUTH_EMPLOYEE_REGISTER_SUM = "Register new employee account with some information";

    // For user authentication =================================================================
    public static final String USER_AUTH_CONTROLLER_TITLE = "USER AUTH MANAGEMENT";
    public static final String USER_AUTH_REGISTER_SUM = "Register new user account with some information";
    public static final String USER_AUTH_LOGIN_SUM = "User login to get token";
    public static final String USER_AUTH_RE_SEND_EMAIL_SUM = "Send email confirmation code a second time or later to update the Confirmation collection";
    public static final String USER_AUTH_SEND_CODE_TO_EMAIL_TO_REGISTER_SUM = "Send email confirmation code to register account";
    public static final String USER_AUTH_SEND_CODE_TO_EMAIL_TO_GET_PWD_SUM = "Send email confirmation code to change password";
    public static final String USER_AUTH_VERIFY_EMAIL_SUM = "Verify confirmation code by email";
    public static final String USER_AUTH_CHANGE_PASSWORD_SUM = "Change password by email when forgot password";
    public static final String USER_AUTH_REFRESH_TOKEN_SUM = "Create new access token and refresh token for user";

    // For user =================================================================
    public static final String USER_CONTROLLER_TITLE = "USER MANAGEMENT";
    public static final String USER_GET_BY_ID_SUM = "Get a user by user id";
    public static final String USER_GET_ALL_SUM = "Get all users";
    public static final String USER_CHANGE_PWD_SUM = "Change password by user id";
    public static final String USER_UPDATE_BY_ID_SUM = "Update user's profile by user id";
    public static final String USER_CHECK_EXISTED_BY_EMAIL_SUM = "Check user existence by email";
    // For address =================================================================
    public static final String ADDRESS_CONTROLLER_TITLE = "ADDRESS MANAGEMENT";
    public static final String ADDRESS_ADD_ADDRESS_BY_ID_SUM = "Add new address for user";
    public static final String ADDRESS_UPDATE_ADDRESS_BY_ID_SUM = "Update address by address id";
    public static final String ADDRESS_DELETE_ADDRESS_BY_ID_SUM = "Delete address by address id";
    public static final String ADDRESS_GET_BY_USER_ID_SUM = "Get all addresses by user id";
    public static final String ADDRESS_GET_DETAILS_BY_ID_SUM = "Get an address details by id";

    // For product =================================================================

    public static final String PRODUCT_CONTROLLER_TITLE = "PRODUCT MANAGEMENT";
    public static final String PRODUCT_CREATE_SUM = "Create a new product";
    public static final String PRODUCT_GET_BY_ID_SUM = "Get a product's details by product id";
    public static final String PRODUCT_GET_ENABLED_BY_ID_SUM = "Get a product enabled by product id";
    public static final String PRODUCT_GET_BY_CATEGORY_ID_SUM = "Get products by category id";
    public static final String PRODUCT_GET_ALL_OR_SEARCH_ENABLED_SUM = "Get all or search products which have enabled";
    public static final String PRODUCT_GET_ALL_SUM = "Get product list or search product list by code, name, description or filter by category id, status and then paging";
    public static final String PRODUCT_DELETE_BY_ID_SUM = "Delete a product by product id";
    public static final String PRODUCT_SOME_DELETE_BY_ID_SUM = "Delete some products by product id";

    public static final String PRODUCT_UPDATE_BY_ID_SUM = "Update product's information by product id";
    public static final String PRODUCT_GET_TOP_ORDER_QUANTITY_SUM = "Get top products and sort by quantity order";
    public static final String PRODUCT_POST_IMPORT_FILE_SUM = "Create products from imported file";

    // For category =================================================================

    public static final String CATEGORY_CONTROLLER_TITLE = "CATEGORY MANAGEMENT";
    public static final String CATEGORY_CREATE_SUM = "Create a new category";
    public static final String CATEGORY_GET_BY_ID_SUM = "Get a category by category id";
    public static final String CATEGORY_GET_ALL_SUM = "Get all categories";
    public static final String CATEGORY_GET_ALL_WITHOUT_DELETED_SUM = "Get all categories without disabled";
    public static final String CATEGORY_UPDATE_BY_ID_SUM = "Update category's information by category id";
    public static final String CATEGORY_DELETE_BY_ID_SUM = "Delete a category by category id";

    // For transaction =======================================================

    public static final String TRANSACTION_CONTROLLER_TITLE = "TRANSACTION MANAGEMENT";
    public static final String TRANSACTION_UPDATE_BY_ID_SUM = "Update transaction's information by transaction id after paid/canceled at vnpay page";
    public static final String TRANSACTION_UPDATE_SUCCESS_STATUS_BY_ID_SUM = "Update transaction's status successfully by transaction id";
    public static final String TRANSACTION_GET_REVENUE_BY_TIME_SUM = "Get all revenue of store by time [day, month, year]";
    public static final String TRANSACTION_GET_REVENUE_CURRENT_DATE_SUM = "Get revenue of store at current date";


    // For Order =============================================================

    public static final String ORDER_CONTROLLER_TITLE = "ORDER MANAGEMENT";
    public static final String ORDER_UPDATE_EVENT_SUM = "Add an order event to the status line";
    public static final String ORDER_CREATE_SHIPPING_SUM = "Create a new shipping order";
    public static final String ORDER_CREATE_ONSITE_SUM = "Create a new onsite order";
    public static final String ORDER_GET_ALL_IN_DAY_SUM = "Get all shipping orders in current day for employee";
    public static final String ORDER_GET_ALL_BY_TYPE_AND_STATUS_IN_DAY_SUM = "Get all orders for employee by type and status of order in current day";
    public static final String ORDER_GET_DETAILS_BY_ID_SUM = "Get a details order by order id";
    public static final String ORDER_GET_ORDERS_BY_USER_ID_AND_ORDER_STATUS_SUM = "Get orders history by user id and order status";
    public static final String ORDER_CREATE_REVIEW_SUM = "Create review for order by order id";
    public static final String ORDER_GET_STATUS_LINE_SUM = "Get order status line by order id";
    public static final String ORDER_GET_ALL_ORDER_HISTORY_FOR_EMPLOYEE_SUM = "Get all order history for employee by page and size or search by code, customerName, customerCode, phoneNumber, recipientName";
    public static final String ORDER_GET_ORDER_QUANTITY_BY_STATUS_SUM = "Get order's quantity by status at today";
    public static final String ORDER_GET_LIST_SUM = "Get order list for admin";

    // For Employee =============================================================
    public static final String EMPLOYEE_CONTROLLER_TITLE = "EMPLOYEE MANAGEMENT";
    public static final String EMPLOYEE_UPDATE_BY_ID_SUM = "Update employee's information by employee id";
    public static final String EMPLOYEE_UPDATE_PASSWORD_BY_ID_SUM = "Update employee's new password by employee id for admin";

    public static final String EMPLOYEE_DELETE_BY_ID_SUM = "Delete a employee by employee id";

    public static final String EMPLOYEE_GET_BY_ID_SUM = "Get a employee by employee id";
    public static final String EMPLOYEE_GET_ALL_SUM = "Get all employees";
    public static final String EMPLOYEE_UPDATE_PASSWORD_SUM = "Update new password for employee by employee id for employee";

    // For Branch =============================================================
    public static final String BRANCH_CONTROLLER_TITLE = "BRANCH MANAGEMENT";
    public static final String BRANCH_CREATE_SUM = "Create a new branch";
    public static final String BRANCH_UPDATE_BY_ID_SUM = "Update branch's information by id";
    public static final String BRANCH_DELETE_BY_ID_SUM = "Delete branch's information by id";
    public static final String BRANCH_GET_ALL_SUM = "Get branch list";

    // For banner =================================================================
    public static final String BANNER_CONTROLLER_TITLE = "BANNER MANAGEMENT";
    public static final String BANNER_CREATE_SUM = "Create a new banner";
    public static final String BANNER_UPDATE_BY_ID_SUM = "Update a banner by id";
    public static final String BANNER_DELETE_BY_ID_SUM = "Delete a banner by id";
    public static final String BANNER_GET_LIST_SUM = "Get banner list";
    public static final String BANNER_GET_VISIBLE_LIST_SUM = "Get visible banner list";
    public static final String BANNER_GET_DETAILS_BY_ID_LIST_SUM = "Get banner's details by id";

    // For banner =================================================================
    public static final String COUPON_CONTROLLER_TITLE = "COUPON MANAGEMENT";
    public static final String COUPON_CREATE_SHIPPING_TYPE_SUM = "Create a new shipping coupon";
    public static final String COUPON_CREATE_ORDER_TYPE_SUM = "Create a new shipping coupon";
    public static final String COUPON_CREATE_BUY_GET_PRODUCT_GIFT_SUM = "Create a new coupon to buy a product and get free products";
    public static final String COUPON_UPDATE_MONEY_BY_ID_SUM = "Update a money coupon by id";
    public static final String COUPON_UPDATE_PRODUCT_GIFT_BY_ID_SUM = "Update a product gift coupon by id";
    public static final String COUPON_DELETE_BY_ID_SUM = "Delete a coupon by id";
    public static final String COUPON_GET_RELEASE_LIST_SUM = "Get release coupon list";
    public static final String COUPON_GET_RELEASE_BY_ID_SUM = "Get a release coupon by id";
    public static final String COUPON_GET_LIST_SUM = "Get coupon list";
    public static final String COUPON_GET_BY_ID_SUM = "Get coupon's details by id";


    // For schema properties =====================================================

    public static final String NOT_EMPTY_DES = "Cannot be empty";
    public static final String NOT_BLANK_DES = "Cannot be blank";
    public static final String NOT_NULL_DES = "Cannot be null";
    public static final String OPTIONAL_DES = "Optional";
    public static final String REGEX_DES = "Write correct regex standards";

    public static final String MIN_VALUE_DES = "Min value: ";
    public static final String MIN_LENGTH_DES = "Minimum length: ";
    public static final String MAX_LENGTH_DES = "Minimum length: ";


    public static final String PASSWORD_EX = "123456";
    public static final String PASSWORD_DES = MIN_LENGTH_DES + PASSWORD_LENGTH_MIN + ", " + MAX_LENGTH_DES + PASSWORD_LENGTH_MAX;
    public static final String CATEGORY_NAME_EX = "Coffee";
    public static final String PRODUCT_NAME_EX = "Coca cola";
    public static final String PRODUCT_TYPE_EX = "FOOD";
    public static final String PRODUCT_PRICE_EX = "100000";

    public static final String PRODUCT_DESCRIPTION_EX = "Coca Cola is made from Pepsi";

    public static final String TOPPING_NAME_EX = "Ca cao" ;

    public static final String OBJECT_ID_EX = "3504503451AV8405345" ;
    public static final String OBJECT_ID_ARRAY_EX = "[\"3504503451AV8405345\", \"3504503451AV8405345\"]" ;
    public static final String TOPPING_PRICE_EX = TOPPING_PRICE_MIN + 1000 + "" ;
    public static final String EMAIL_EX = "nva@gmail.com" ;
    public static final String FIRST_NAME_EX = "An" ;
    public static final String LAST_NAME_EX = "Nguyen" ;
    public static final String FIRST_NAME_EMPLOYEE_EX = "Tao" ;
    public static final String LAST_NAME_EMPLOYEE_EX = "Thao" ;
    public static final String BIRTH_DATE_EX = "2002-01-01";
    public static final String GENDER_EX = "FEMALE";
    public static final String PHONE_NUMBER_EX = "0123456789";
    public static final String CODE_EX = "0000";
    public static final String VERIFY_NUMBER_MSG_EX = "Your authentication code is: 0000";
    public static final String USERNAME_EMPLOYEE_EX = "nva6112002";
    public static final String PASSWORD_EMPLOYEE_EX = "112233";
    public static final String RATING_EX = "4";
    public static final String CONTENT_EX = "It's so good";
    public static final String ORDER_STATUS_EX = "CREATED";
    public static final String ORDER_STATUS_DES_EX = "Order is created";
    public static final String PRODUCT_QUANTITY_EX = "3";
    public static final String COUPON_QUANTITY_EX = "3";
    public static final String PRODUCT_NOTE_EX = "This is note";
    public static final String INVOICE_NOTE_EX = "23654128";
    public static final String TOTAL_PAID_EX = "10234";
    public static final String LONGITUDE_EX = "10234";
    public static final String LATITUDE_EX = "10234";
    public static final String ADDRESS_NOTE_EX = "This is my house";
    public static final String RECIPIENT_NAME_EX = "Luu Bang";
    public static final String ORDER_NOTE_EX = "Quickly";
    public static final String PAYMENT_STATUS_EX = "PAID";
    public static final String PAYMENT_TYPE_EX = "BANKING_VNPAY";
    public static final String REVIEW_DES_EX = "Good service";
    public static final String BOOLEAN_EX = "false";
    public static final String PRODUCT_STATUS_EX = "HIDDEN";
    public static final String USERNAME_EX = "employee";
    public static final String REFRESH_TOKEN_EX = "wesaferg534vbdrbtgertyrrw6b456ertbvw354t";
    public static final String PROVINCE_EX = "Binh Duong";
    public static final String WARD_EX = "Tan Binh";
    public static final String DISTRICT_EX = "Di An";
    public static final String ADDRESS_DETAILS_EX = "Nguyen Thi Tuoi - tan hiep";
    public static final String EMPLOYEE_STATUS_EX = "ACTIVE";
    public static final String CATEGORY_STATUS_EX = "VISIBLE";
    public static final String BANNER_NAME_EX = "New Product 2024";
    public static final String BANNER_STATUS_EX = "VISIBLE";
    public static final String COUPON_CODE_EX = "XUAN2024";
    public static final String COUPON_CODE_LIST_EX = "[\"XUAN2024\", \"XUAN2025\"]" ;
    public static final String COUPON_DESCRIPTION_EX = "Sieu sale";
    public static final String COUPON_STATUS_EX = "UNRELEASED";
    public static final String COUPON_CONDITION_DESCRIPTION_EX = "Giam cho don tu 500k";
    public static final String COUPON_CONDITION_TYPE_EX = "TOTAL_BILL";
    public static final String COUPON_UNIT_EX = "MONEY";
    public static final String DISCOUNT_TARGET_EX = "ORDER_BILL";
    public static final String COUPON_MAX_REDUCTION_EX = "5000";
    public static final String DISCOUNT_VALUE_EX = "5000";
    public static final String OPERATOR_EX = "GREATER";
    public static final String SHIPPING_FEE_EX = "15000";
    public static final String DATE_ISO_EX = "2024-01-14T08:38:00.715Z";
    public static final String APPLICABLE_CUSTOMER_TYPE = "ALL";
    public static final String MIN_PURCHASE_TYPE = "NONE";
    public static final String INTEGER_VALUE_EX = "15000";
    public static final String USAGE_CONDITION_EX = "QUANTITY";
    public static final String COMBINATION_CONDITION_EX = "PRODUCT";
    public static final String TARGET_OBJECT_CONDITION_EX = "PRODUCT";
    public static final String PRODUCT_SIZE_EX = "SMALL";
}
