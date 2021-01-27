package com.auction_system.exceptions;

public class NoBrokersException extends MyException {
    public static final String MESSAGE = "Exception: No brokers!";

    public NoBrokersException() {
        super(MESSAGE);
    }
}
