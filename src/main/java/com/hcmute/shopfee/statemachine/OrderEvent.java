package com.hcmute.shopfee.statemachine;

public enum OrderEvent {
    ORDER_REFUSE("Order has been cancelled"),
    ORDER_ACCEPT("Order has been confirmed"),
    CANCEL_REQUEST("Order cancellation request submitted"),
    CANCEL_REQUEST_REFUSE("Refuse the request to cancel the application"),
    CANCEL_REQUEST_ACCEPT("Accept the request to cancel the order"),
    READY_SHIPPING("Order has been prepared"),
    START_SHIPPING("Order is shipping"),
    ORDER_FULFILL("Order completed successfully"),
    ORDER_BOOM("Order failed");

    private final String description;
    OrderEvent(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
