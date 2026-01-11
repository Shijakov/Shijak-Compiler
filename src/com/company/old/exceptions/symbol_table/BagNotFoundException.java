package com.company.old.exceptions.symbol_table;

public class BagNotFoundException extends SymbolTableException {
    public BagNotFoundException(String bagName) {
        super("Bag " + bagName + " not found");
    }
}
