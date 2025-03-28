package com.company.exceptions.symbol_table;

public class ScopeNotFoundException extends SymbolTableException {
    public ScopeNotFoundException(Integer id) {
        super(String.format("Scope with id %d not found", id));
    }
}
