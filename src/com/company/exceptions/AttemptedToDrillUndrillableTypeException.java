package com.company.exceptions;

import com.company.symbol_table.variable_types.VarType;

public class AttemptedToDrillUndrillableTypeException extends Exception{
    public AttemptedToDrillUndrillableTypeException(VarType type) {
        super("Attempted to drill undrillable type " + type);
    }
}
