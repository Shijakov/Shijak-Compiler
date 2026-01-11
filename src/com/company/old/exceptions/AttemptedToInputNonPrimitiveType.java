package com.company.old.exceptions;

import com.company.old.symbol_table.variable_types.VarType;

public class AttemptedToInputNonPrimitiveType extends LineInfoException {
    public AttemptedToInputNonPrimitiveType(VarType type, int line) {
        super("Attempted do drill non-primitive type " + type, line );
    }
}
