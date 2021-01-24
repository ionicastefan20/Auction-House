package com.auction_system.exceptions;

public class InvalidPriceException extends Exception {
    public static final String MESSAGE = "Exception: Invalid price!";

    public InvalidPriceException() {
        super(MESSAGE);
    }
}
