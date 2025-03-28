package com.company.exceptions;

import com.company.symbol_table.variable_types.VarType;

public class InvalidTypesForBinaryOperatorException extends LineInfoException {
    public InvalidTypesForBinaryOperatorException(String operator, VarType left, VarType right, int line) {
        super(String.format("Invalid types for \"%s\" operator, left: %s, right: %s", operator, left, right), line);
    }
}
