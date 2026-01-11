package com.company.compiler.common.token;

import com.company.compiler.common.symbol.Terminal;

public class Token extends Terminal {
    public Token(String regex) {
        super(regex);
    }

    public String getRegex() {
        return this.name;
    }

    @Override
    protected String className() {
        return Token.class.getName();
    }
}
