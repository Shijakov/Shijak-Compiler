package com.company.exceptions.symbol_table;

public class FunctionNotFoundException extends SymbolTableException {
    public FunctionNotFoundException(String functionName) {
        super("Function " + functionName + " not found");
    }
}
