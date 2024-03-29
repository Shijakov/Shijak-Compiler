package com.company.exceptions;

public class BreakNotInLoopException extends Exception {
    public BreakNotInLoopException() {
        super("break outside of loop");
    }
}
