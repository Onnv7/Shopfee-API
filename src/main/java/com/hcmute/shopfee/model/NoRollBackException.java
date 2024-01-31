package com.hcmute.shopfee.model;

public class NoRollBackException extends RuntimeException{
    public NoRollBackException(String message) {
        super(message);
    }
}
