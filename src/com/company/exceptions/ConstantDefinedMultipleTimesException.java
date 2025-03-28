package com.company.exceptions;

public class ConstantDefinedMultipleTimesException extends LineInfoException {
    public ConstantDefinedMultipleTimesException(String constantName, int line) {
        super("Constant " + constantName + " defined multiple times", line);
    }
}
