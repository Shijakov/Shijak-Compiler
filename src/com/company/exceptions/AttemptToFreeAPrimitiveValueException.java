package com.company.exceptions;

import com.company.symbol_table.variable_types.VarType;

public class AttemptToFreeAPrimitiveValueException extends LineInfoException {
    public AttemptToFreeAPrimitiveValueException(VarType type, int line) {
        super("Attempt to free instance of type " + type, line);
    }
}
