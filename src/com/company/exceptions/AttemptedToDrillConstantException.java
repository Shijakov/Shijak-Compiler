package com.company.exceptions;

public class AttemptedToDrillConstantException extends LineInfoException {
    public AttemptedToDrillConstantException(String constantName, int line) {
        super("Attempted to drill constant " + constantName, line);
    }
}
