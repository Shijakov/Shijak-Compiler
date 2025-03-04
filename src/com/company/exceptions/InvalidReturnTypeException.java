package com.company.exceptions;

import com.company.symbol_table.variable_types.VarType;

public class InvalidReturnTypeException extends Exception{
    public InvalidReturnTypeException(VarType expected, VarType actual) {
        super(String.format("Invalid return type. Expected: %s, Got: %s", expected, actual));
    }
}
