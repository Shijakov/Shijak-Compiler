package com.company.exceptions;

import com.company.symbol_table.variable_types.VarType;

public class InvalidReturnTypeException extends LineInfoException {
    public InvalidReturnTypeException(VarType expected, VarType actual, int line) {
        super(String.format("Invalid return type. Expected: %s, Got: %s", expected, actual), line);
    }
}
