package com.auction_system.exceptions;

public class ProductDoesNotExistException extends Exception {
    public static final String MESSAGE1 = "Exception: The product (";
    public static final String MESSAGE2 = ") does not exist!";

    public ProductDoesNotExistException(int id) {
        super(MESSAGE1 + id + MESSAGE2);
    }
}
