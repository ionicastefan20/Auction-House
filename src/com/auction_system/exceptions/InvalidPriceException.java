package com.auction_system.exceptions;

public class InvalidPriceException extends MyException {
    public static final String MESSAGE = "Exception: Invalid price!";

    public InvalidPriceException() {
        super(MESSAGE);
    }
}
