package com.company.exceptions;

public class CannotAssignValueToPointerException extends LineInfoException {
    public CannotAssignValueToPointerException(String identifier, int line) {
        super("Cannot assign value to pointer " + identifier, line);
    }
}
