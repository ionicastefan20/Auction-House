package com.auction_system.exceptions;

public class UserDoesNotExistException extends MyException {
    public static final String MESSAGE1 = "Exception: The username (";
    public static final String MESSAGE2 = ") does not exist!";

    public UserDoesNotExistException(String username) {
        super(MESSAGE1 + username + MESSAGE2);
    }
}
