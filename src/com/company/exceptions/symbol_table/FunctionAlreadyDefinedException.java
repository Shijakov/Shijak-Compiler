package com.company.exceptions.symbol_table;

public class FunctionAlreadyDefinedException extends SymbolTableException {
    public FunctionAlreadyDefinedException(String functionName) {
        super("Function " + functionName + " already defined");
    }
}
