package com.company.old.exceptions.symbol_table;

public class BagAlreadyDefinedException extends SymbolTableException {
    public BagAlreadyDefinedException(String bagName) {
        super("Bag  " + bagName + " already defined");
    }
}
