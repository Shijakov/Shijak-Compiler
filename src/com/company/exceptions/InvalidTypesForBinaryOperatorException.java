package com.company.exceptions;

import com.company.symbol_table.VarType;

public class InvalidTypesForBinaryOperatorException extends Exception {
    public InvalidTypesForBinaryOperatorException(String operator, VarType left, VarType right) {
        super(String.format("Invalid types for \"%s\" operator, left: %s, right: %s", operator, left, right));
    }
}
