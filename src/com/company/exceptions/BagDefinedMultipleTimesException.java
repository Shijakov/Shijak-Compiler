package com.company.exceptions;

public class BagDefinedMultipleTimesException extends LineInfoException {
    public BagDefinedMultipleTimesException(String funName, int line) {
        super("Bag " + funName + " defined multiple times", line);
    }
}
