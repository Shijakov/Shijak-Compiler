package com.company.exceptions;

public class AttemptToChangeConstValueException extends LineInfoException {
    public AttemptToChangeConstValueException(String var, int line) {
        super("Attempt to change value of const variable " + var, line);
    }
}
