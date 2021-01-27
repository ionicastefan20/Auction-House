package com.auction_system.exceptions;

public class UserAlreadyExistsException extends MyException {
    public static final String MESSAGE1 = "Exception: The username (";
    public static final String MESSAGE2 = ") already exists!";

    public UserAlreadyExistsException(String username) {
        super(MESSAGE1 + username + MESSAGE2);
    }
}
