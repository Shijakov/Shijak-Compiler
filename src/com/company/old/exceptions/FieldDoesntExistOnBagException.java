package com.company.old.exceptions;

public class FieldDoesntExistOnBagException extends LineInfoException {
    public FieldDoesntExistOnBagException(String bagName, String fieldName, int line) {
        super("Field " + fieldName + " does not exist on bag " + bagName, line);
    }
}
