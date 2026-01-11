package com.company.old.exceptions.symbol_table;

import com.company.old.exceptions.LineInfoException;

public class VariableDefinedMultipleTimesException extends LineInfoException {
    public VariableDefinedMultipleTimesException(String varName, int line) {
        super("Variable " + varName + " defined multiple times", line);
    }
}
