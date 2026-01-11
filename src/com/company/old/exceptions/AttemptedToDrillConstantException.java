package com.company.old.exceptions;

public class AttemptedToDrillConstantException extends LineInfoException {
    public AttemptedToDrillConstantException(String constantName, int line) {
        super("Attempted to drill constant " + constantName, line);
    }
}
