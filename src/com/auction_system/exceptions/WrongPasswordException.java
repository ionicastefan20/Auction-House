package com.auction_system.exceptions;

public class WrongPasswordException extends MyException {
    public static final String MESSAGE = "Exception: Wrong password!";

    public WrongPasswordException() {
        super(MESSAGE);
    }
}
