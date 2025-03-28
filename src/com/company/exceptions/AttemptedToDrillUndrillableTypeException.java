package com.company.exceptions;

import com.company.symbol_table.variable_types.VarType;

public class AttemptedToDrillUndrillableTypeException extends LineInfoException {
    public AttemptedToDrillUndrillableTypeException(VarType type, int line) {
        super("Attempted to drill undrillable type " + type, line);
    }
}
