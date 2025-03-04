package com.company.exceptions;

public class BagDefinedMultipleTimesException extends Exception {
    public BagDefinedMultipleTimesException(String funName) {
        super(String.format("Bag %s defined multiple times", funName));
    }
}
