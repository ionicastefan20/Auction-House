package com.auction_system.exceptions;

public class InvalidOptionException extends MyException {
    public static final String MESSAGE = "Exception: Invalid option!";

    public InvalidOptionException() {
        super(MESSAGE);
    }
}
