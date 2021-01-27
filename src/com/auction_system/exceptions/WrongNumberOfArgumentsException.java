package com.auction_system.exceptions;

public class WrongNumberOfArgumentsException extends MyException {
    public static final String MESSAGE = "Exception: Wrong number of arguments!";

    public WrongNumberOfArgumentsException() {
        super(MESSAGE);
    }
}
