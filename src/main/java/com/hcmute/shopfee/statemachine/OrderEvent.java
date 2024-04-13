package com.hcmute.shopfee.statemachine;

public enum OrderEvent {
    ORDER_REFUSE,
    ORDER_ACCEPT,
    CANCEL_REQUEST,
    CANCEL_REQUEST_REFUSE,
    CANCEL_REQUEST_ACCEPT,
    READY_SHIPPING,
    START_SHIPPING,
    ORDER_FULFILL,
    ORDER_BOOM

}
