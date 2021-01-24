package com.auction_system.exceptions;

public class PermissionDeniedException extends Exception {
    public static final String MESSAGE = "Exception: Permission denied!";

    public PermissionDeniedException() {
        super(MESSAGE);
    }
}
