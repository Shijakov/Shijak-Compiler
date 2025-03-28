package com.company.exceptions;

public class NotABagException extends LineInfoException {
    public NotABagException(String identifier, int line) {
        super("Not a bag: " + identifier, line);
    }
}
