package com.hcmute.shopfee.constant;


public class ShopfeeConstant {
    public static final int ONSITE_OPERATING_RANGE = 12000;
    // fixed data string
    public static final String COIN_REFUND_CANCELLED_ORDER = "Coins refunded from canceled paid orders";
    public static final String DEDUCT_COIN_TO_PAY = "Deduct coins to pay bill ";
    public static final String REVIEW_COIN = "Bonus coins for product reviews";
    public static final String REFUND_COIN_ORDER = "Coins refunded when order is canceled";

    // error message
    public static final String TIMEOUT_REQUEST_REFUND_ERR_MSG = "A refund request cannot be submitted after 3 hours from the time the order is successfully delivered";
    public static final String TOKEN_NOT_FOUND_ERR_MSG = "Token not found in database";

    // config
    public static final int OPERATING_RANGE_DISTANCE = 12000;
    public static final int HOURS_REQUEST_REFUND = 3;
    public static int ACCESS_TOKEN_EXPIRE_MINUTES_TIME = 60 * 24 * 7;
    public static final int REFRESH_TOKEN_EXPIRE_MINUTES_TIME = 60 * 24 * 7;
    public static final int TIMEOUT_REFUSE_ORDER_MINUTES = 30;
    public static final int TIMEOUT_VNPAY_TRANSACTION = 17;
    public static final int TIMEOUT_ZALO_TRANSACTION = 15;
}
