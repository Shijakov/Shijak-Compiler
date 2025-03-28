package com.company.exceptions;

import com.company.symbol_table.variable_types.VarType;

public class AttemptedToOutputNonPrimitiveType extends LineInfoException {
    public AttemptedToOutputNonPrimitiveType(VarType type, int line){
        super("Attempted to output non-primitive type " + type, line);
    }
}
