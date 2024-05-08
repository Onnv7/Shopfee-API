package com.hcmute.shopfee.statemachine;

public enum OrderEvent {
    PAYMENT_FAILED("Your order {0} payment failed"), // orderId
    USER_REFUSE("Order {0} was canceled. Check now"), // orderId
    EMPLOYEE_ORDER_REFUSE("Your order {0} has been cancelled"), // orderId
    ACCEPT_ORDER("Your order {0} was accepted by {1}. Please wait for us to process your order."), // orderId employeeId
    REQUEST_CANCEL("Order cancellation request submitted"),
    REFUSE_ORDER_CANCELLATION("Your request cancel in order {0} was refused"), // orderId
    ACCEPT_ORDER_CANCELLATION("Your request cancel in order {0} was accepted"), // orderId
    PREPARED("Your order {0} was prepared"), // orderId
    START_SHIPPING("Your order {0} is delivering. Please pay attention to your phone in case the shipper call you."), // orderId
    FULFILL("You have get your order {0}. Thank you for choosing Shopfee."), // orderId
    BOOM("Your order {0} was marked as boom"); // orderId

    private final String notificationMsg;
    OrderEvent(String notificationMsg) {

        this.notificationMsg = notificationMsg;
    }

    public String getNotificationMsg() {
        return notificationMsg;
    }
}
