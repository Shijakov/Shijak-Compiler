package com.company.exceptions;

public class VariableAlreadyDeclaredException extends LineInfoException {
    public VariableAlreadyDeclaredException(String varName, int line) {
        super("Multiple declares of variable " + varName, line);
    }
}
