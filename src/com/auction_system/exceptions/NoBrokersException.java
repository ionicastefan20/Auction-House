package com.auction_system.exceptions;

public class NoBrokersException extends Exception {
    public static final String MESSAGE = "Exception: No brokers!";

    public NoBrokersException() {
        super(MESSAGE);
    }
}
