package com.company.exceptions;

public class ContinueNotInLoopException extends Exception {
    public ContinueNotInLoopException() {
        super("continue outside of loop");
    }
}
