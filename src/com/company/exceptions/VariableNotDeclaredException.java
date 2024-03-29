package com.company.exceptions;

public class VariableNotDeclaredException extends Exception {
    public VariableNotDeclaredException(String varName) {
        super(String.format("Variable %s not declared", varName));
    }
}
