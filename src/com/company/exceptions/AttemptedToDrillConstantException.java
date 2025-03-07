package com.company.exceptions;

public class AttemptedToDrillConstantException extends Exception {
    public AttemptedToDrillConstantException(String constantName) {
        super(constantName);
    }
}
