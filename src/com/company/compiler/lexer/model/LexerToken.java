package com.company.compiler.lexer.model;

import com.company.compiler.common.token.Token;

public class LexerToken implements Comparable<LexerToken> {
    private final Token token;
    private final int priority;
    private final boolean shouldIgnore;

    public static final int HIGH_PRIORITY = 10;
    public static final int LOW_PRIORITY = -10;

    public LexerToken(Token token, int priority, boolean shouldIgnore) {
        this.token = token;
        this.priority = priority;
        this.shouldIgnore = shouldIgnore;
    }

    public static LexerToken from(Token token) {
        return new LexerToken(token, HIGH_PRIORITY, false);
    }

    public static LexerToken ignored(Token token) {
        return new LexerToken(token, HIGH_PRIORITY, true);
    }

    public static LexerToken lowPriority(Token token) {
        return new LexerToken(token, LOW_PRIORITY, false);
    }

    public Token getToken() {
        return token;
    }

    public boolean shouldIgnore() {
        return shouldIgnore;
    }

    @Override
    public int compareTo(LexerToken o) {
        return priority - o.priority;
    }
}
