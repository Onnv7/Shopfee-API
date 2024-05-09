package com.hcmute.shopfee.kafka;

public class KafkaConstant {
    public static final String SEND_CODE_EMAIL_TOPIC = "send-code-email";
    public static final String SEND_USER_BLOCKED_EMAIL_TOPIC = "send-user-blocked-email";
    public static final String USER_ORDER_NOTIFICATION_TOPIC = "user-order-notification";
    public static final String EMPLOYEE_ORDER_NOTIFICATION_TOPIC = "employee-order-notification";

    public static final String SEND_EMAIL_CONSUMER_GROUP_ID = "send-code-email-group";
    public static final String SEND_USER_BLOCKED_EMAIL_CONSUMER_GROUP_ID = "send-user-blocked-email-group";
    public static final String USER_ORDER_NOTIFICATION_GROUP_ID = "user-order-notification-group";
    public static final String EMPLOYEE_ORDER_NOTIFICATION_GROUP_ID = "employee-order-notification-group";
}
