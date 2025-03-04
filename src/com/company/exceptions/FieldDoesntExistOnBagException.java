package com.company.exceptions;

public class FieldDoesntExistOnBagException extends Exception{
    public FieldDoesntExistOnBagException(String bagName, String fieldName) {
        super("Field " + fieldName + " does not exist on bag " + bagName);
    }
}
