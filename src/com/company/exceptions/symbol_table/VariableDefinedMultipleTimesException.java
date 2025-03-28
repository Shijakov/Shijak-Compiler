package com.company.exceptions.symbol_table;

import com.company.exceptions.LineInfoException;

public class VariableDefinedMultipleTimesException extends LineInfoException {
    public VariableDefinedMultipleTimesException(String varName, int line) {
        super("Variable " + varName + " defined multiple times", line);
    }
}
