package com.company.exceptions;

import com.company.symbol_table.variable_types.VarType;

public class AttemptToFreeAPrimitiveValueException extends Exception{
    public AttemptToFreeAPrimitiveValueException(String identifier) {
        super(String.format("Attempt to free variable %s which is of primitive type", identifier));
    }

    public AttemptToFreeAPrimitiveValueException(VarType type) {
        super("Attempt to free instance of type " + type);
    }
}
