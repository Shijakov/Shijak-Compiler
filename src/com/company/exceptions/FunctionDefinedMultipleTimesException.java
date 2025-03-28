package com.company.exceptions;

public class FunctionDefinedMultipleTimesException extends LineInfoException {
    public FunctionDefinedMultipleTimesException(String funName, int line) {
        super("Function " + funName + " defined multiple times", line);
    }
}
