package com.hcmute.shopfee.statemachine;

public enum OrderEvent {
    PAYMENT_FAILED("Your order {0} payment failed"), // orderId
    ORDER_REFUSE("Order {0} was canceled. Check now"), // orderId
    EMPLOYEE_ORDER_REFUSE("Your order {0} has been cancelled"), // orderId
    ORDER_ACCEPT("Your order {0} was accepted by {1}. Please wait for us to process your order."), // orderId employeeId
    CANCEL_REQUEST("Order cancellation request submitted"),
    CANCEL_REQUEST_REFUSE("Your request cancel in order {0} was refused"), // orderId
    CANCEL_REQUEST_ACCEPT("Your request cancel in order {0} was accepted"), // orderId
    READY_SHIPPING("Your order {0} was prepared"), // orderId
    START_SHIPPING("Your order {0} is delivering. Please pay attention to your phone in case the shipper call you."), // orderId
    ORDER_FULFILL("You have get your order {0}. Thank you for choosing Shopfee."), // orderId
    ORDER_BOOM("Your order {0} was marked as boom"); // orderId

    private final String notificationMsg;
    OrderEvent(String notificationMsg) {

        this.notificationMsg = notificationMsg;
    }

    public String getNotificationMsg() {
        return notificationMsg;
    }
}
