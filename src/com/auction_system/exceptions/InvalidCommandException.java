package com.auction_system.exceptions;

public class InvalidCommandException extends Exception {
    public static final String MESSAGE = "Exception: Invalid Command!";

    public InvalidCommandException() {
        super(MESSAGE);
    }
}
