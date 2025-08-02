package com.company.compiler.lexer;

public class MatchedToken {
    private final TokenInterface token;
    private final String value;
    private final int line;

    public MatchedToken(TokenInterface token, String value, int line) {
        this.token = token;
        this.value = value;
        this.line = line;
    }

    public TokenInterface getToken() {
        return token;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }
}
