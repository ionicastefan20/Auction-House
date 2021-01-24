package com.auction_system.exceptions;

public class NotConnectedException extends Exception {
    public static final String MESSAGE = "Exception: Not connected!";

    public NotConnectedException() {
        super(MESSAGE);
    }
}
