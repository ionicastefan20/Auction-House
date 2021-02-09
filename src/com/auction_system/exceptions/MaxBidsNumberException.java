package com.auction_system.exceptions;

public class MaxBidsNumberException extends MyException {
    public static final String MESSAGE1 = "Exception: The auction on product (";
    public static final String MESSAGE2 = ") reached the maximum number of participants!";

    public MaxBidsNumberException(int id) {
        super(MESSAGE1 + id + MESSAGE2);
    }
}
