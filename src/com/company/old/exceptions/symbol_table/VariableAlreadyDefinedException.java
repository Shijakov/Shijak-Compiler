package com.company.old.exceptions.symbol_table;

public class VariableAlreadyDefinedException extends SymbolTableException {
    public VariableAlreadyDefinedException(String variableName) {
        super("Variable " + variableName + " already defined");
    }
}
