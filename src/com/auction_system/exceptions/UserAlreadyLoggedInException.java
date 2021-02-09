package com.auction_system.exceptions;

public class UserAlreadyLoggedInException extends MyException {
    public static final String MESSAGE1 = "Exception: The username (";
    public static final String MESSAGE2 = ") has already logged in!";

    public UserAlreadyLoggedInException(String username) {
        super(MESSAGE1 + username + MESSAGE2);
    }
}
