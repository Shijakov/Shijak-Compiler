package com.company.exceptions;

public class BreakNotInLoopException extends LineInfoException {
    public BreakNotInLoopException(int line) {
        super("break statement outside of loop", line);
    }
}
