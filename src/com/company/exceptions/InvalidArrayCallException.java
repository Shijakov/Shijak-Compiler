package com.company.exceptions;

public class InvalidArrayCallException extends Exception {
    public InvalidArrayCallException(String identifier) {
        super(String.format("Invalid array call: %s", identifier));
    }
}
