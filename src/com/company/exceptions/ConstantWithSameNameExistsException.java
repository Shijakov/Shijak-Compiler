package com.company.exceptions;

public class ConstantWithSameNameExistsException extends Exception{
    public ConstantWithSameNameExistsException(String varName) {
        super(String.format("Constant with same name \"%s\" defined", varName));
    }
}
