package com.auction_system.exceptions;

public class WrongPasswordException extends Exception {
    public static final String MESSAGE = "Exception: Wrong password!";

    public WrongPasswordException() {
        super(MESSAGE);
    }
}
