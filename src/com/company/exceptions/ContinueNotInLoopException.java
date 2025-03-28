package com.company.exceptions;

public class ContinueNotInLoopException extends LineInfoException {
    public ContinueNotInLoopException(int line) {
        super("continue statement outside of loop", line);
    }
}
