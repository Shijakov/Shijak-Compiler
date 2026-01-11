package com.company.old.exceptions;

public class InHasNoValueException extends LineInfoException {
    public InHasNoValueException(int line) {
        super("In has no value", line);
    }
}
