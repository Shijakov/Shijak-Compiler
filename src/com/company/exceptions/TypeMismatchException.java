package com.company.exceptions;

import com.company.symbol_table.variable_types.VarType;

public class TypeMismatchException extends Exception {
    public TypeMismatchException(VarType expected, VarType actual) {
        super(String.format("Type mismatch exception. Expected: %s, Got: %s", expected, actual));
    }
}
