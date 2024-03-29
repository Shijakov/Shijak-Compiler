package com.company.exceptions;

public class CannotAssignValueToPointerException extends Exception {
    public CannotAssignValueToPointerException(String identifier) {
        super(String.format("Cannot assign value to pointer %s", identifier));
    }
}
