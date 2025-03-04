package com.company.exceptions;

import com.company.symbol_table.variable_types.VarType;

public class AttemptedToOutputNonPrimitiveType extends Exception {
    public AttemptedToOutputNonPrimitiveType(VarType type){
        super(type.toString());
    }
}
