package com.company.exceptions;

import com.company.symbol_table.variable_types.VarType;

public class AttemptedToInputNonPrimitiveType extends LineInfoException {
    public AttemptedToInputNonPrimitiveType(VarType type, int line) {
        super("Attempted do drill non-primitive type " + type, line );
    }
}
