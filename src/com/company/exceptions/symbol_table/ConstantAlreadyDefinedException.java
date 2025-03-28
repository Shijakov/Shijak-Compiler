package com.company.exceptions.symbol_table;

public class ConstantAlreadyDefinedException extends SymbolTableException {
    public ConstantAlreadyDefinedException(String constName) {
        super("Constant " + constName + " is already defined");
    }
}
