package com.company.exceptions;

public class InvalidArrayOperation extends Exception {
    public InvalidArrayOperation(String operator) {
        super("Invalid operation with array: " + operator);
    }
}
