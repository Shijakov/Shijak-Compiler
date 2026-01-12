package com.company.compiler.common.symbol;

public class Terminal extends Symbol {
    public Terminal(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "Terminal(" + name + ")";
    }

    @Override
    protected String className() {
        return Terminal.class.getName();
    }
}
