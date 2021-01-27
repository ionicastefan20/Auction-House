package com.auction_system.exceptions;

public class BidOnTheSameProductException extends MyException {
    public static final String MESSAGE1 = "Exception: Already bid on product (";
    public static final String MESSAGE2 = ")!";

    public BidOnTheSameProductException(int id) {
        super(MESSAGE1 + id + MESSAGE2);
    }
}
