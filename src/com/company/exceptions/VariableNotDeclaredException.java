package com.company.exceptions;

public class VariableNotDeclaredException extends LineInfoException {
    public VariableNotDeclaredException(String varName, int line) {
        super(String.format("Variable %s not declared", varName), line);
    }
}
