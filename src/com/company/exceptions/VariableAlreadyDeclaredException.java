package com.company.exceptions;

public class VariableAlreadyDeclaredException extends Exception{
    public VariableAlreadyDeclaredException(String varName) {
        super(String.format("Multiple declares of variable %s", varName));
    }
}
