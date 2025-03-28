package com.company.exceptions;

public class ConstantWithSameNameExistsException extends LineInfoException{
    public ConstantWithSameNameExistsException(String varName, int line) {
        super("Constant with same name \"" + varName + "\" defined", line);
    }
}
