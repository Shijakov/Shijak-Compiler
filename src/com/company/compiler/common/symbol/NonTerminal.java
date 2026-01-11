package com.company.compiler.common.symbol;

public class NonTerminal extends Symbol {
    public NonTerminal(String name) {
        super(name);
    }

    @Override
    protected String className() {
        return NonTerminal.class.getName();
    }

    @Override
    public String toString() {
        return "NonTerminal(" +
                name +
                ')';
    }
}
