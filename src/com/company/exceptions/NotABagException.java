package com.company.exceptions;

public class NotABagException extends Exception {
    public NotABagException(String identifier) {
        super(identifier);
    }
}
