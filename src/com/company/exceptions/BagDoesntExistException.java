package com.company.exceptions;

public class BagDoesntExistException extends LineInfoException {
    public BagDoesntExistException(String bagName, int line) {
        super("Bag " + bagName + " doesn't exist", line);
    }
}
