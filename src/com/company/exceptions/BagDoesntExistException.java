package com.company.exceptions;

public class BagDoesntExistException extends Exception {
    public BagDoesntExistException(String bagName) {
        super("Bag " + bagName + " doesn't exist");
    }
}
