package com.company.exceptions;

public class AttemptToChangeConstValueException extends Exception {
    public AttemptToChangeConstValueException(String var) {
        super(String.format("Attempt to change value of const variable %s", var));
    }
}
