package com.company.exceptions;

public class NotAnArrayException extends Exception {
    public NotAnArrayException(String identifier) {
        super(identifier);
    }
}
