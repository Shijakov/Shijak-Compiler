package com.company.old.exceptions.symbol_table;

public class VariableNotFoundException extends SymbolTableException {
    public VariableNotFoundException(String variableName) {
        super("Variable " + variableName + " not found");
    }
}
