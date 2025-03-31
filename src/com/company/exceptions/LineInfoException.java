package com.company.exceptions;

public abstract class LineInfoException extends Exception {
    public LineInfoException(String message, int line) {
        super(String.format("%s - Occured on line %d", message, line));
    }
}
