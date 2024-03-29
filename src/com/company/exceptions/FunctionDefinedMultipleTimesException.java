package com.company.exceptions;

public class FunctionDefinedMultipleTimesException extends Exception {
    public FunctionDefinedMultipleTimesException(String funName) {
        super(String.format("Function %s defined multiple times", funName));
    }
}
