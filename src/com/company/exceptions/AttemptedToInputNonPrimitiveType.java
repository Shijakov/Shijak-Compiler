package com.company.exceptions;

import com.company.symbol_table.variable_types.VarType;

public class AttemptedToInputNonPrimitiveType extends Exception {
    public AttemptedToInputNonPrimitiveType(VarType type){
        super(type.toString());
    }
}
