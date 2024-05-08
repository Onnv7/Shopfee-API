package com.hcmute.shopfee.enums;

public enum OrderStatus {
    CREATED("Order has been created"),
    ACCEPTED("Order has been confirmed"),
    CANCELLATION_REQUEST("Order cancellation request submitted"),
    CANCELLATION_REQUEST_REFUSED("Refuse the request to cancel the application"),
    CANCELLATION_REQUEST_ACCEPTED("Accept the request to cancel the order"),
    PENDING_PICK_UP("Order has been prepared"),
    IN_DELIVERY("Order is shipping"),
    CANCELED("Order has been cancelled"),
    SUCCEED("Order completed successfully"),
    NOT_RECEIVED("Order failed");

    private final String resultDescription;
    OrderStatus(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public String getResultDescription() {
        return resultDescription;
    }
}
