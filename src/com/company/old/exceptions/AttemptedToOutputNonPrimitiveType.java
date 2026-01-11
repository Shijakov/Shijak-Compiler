package com.company.old.exceptions;

import com.company.old.symbol_table.variable_types.VarType;

public class AttemptedToOutputNonPrimitiveType extends LineInfoException {
    public AttemptedToOutputNonPrimitiveType(VarType type, int line){
        super("Attempted to output non-primitive type " + type, line);
    }
}
