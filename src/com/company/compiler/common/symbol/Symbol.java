package com.company.compiler.common.symbol;

import java.util.Objects;

public abstract class Symbol {
    protected final String name;

    protected Symbol(String name) {
        this.name = name;
    }

    protected String className() {
        return this.getClass().getName();
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Symbol)) return false;
        if (!Objects.equals(className(), ((Symbol) object).className())) return false;
        Symbol symbol = (Symbol) object;
        return Objects.equals(name, symbol.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
