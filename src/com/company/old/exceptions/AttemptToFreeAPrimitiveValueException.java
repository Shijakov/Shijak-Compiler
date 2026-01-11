package com.company.old.exceptions;

import com.company.old.symbol_table.variable_types.VarType;

public class AttemptToFreeAPrimitiveValueException extends LineInfoException {
    public AttemptToFreeAPrimitiveValueException(VarType type, int line) {
        super("Attempt to free instance of type " + type, line);
    }
}
