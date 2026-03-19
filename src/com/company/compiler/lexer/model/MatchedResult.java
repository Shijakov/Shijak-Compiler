package com.company.compiler.lexer.model;

import com.company.compiler.common.token.IdentifierToken;
import com.company.compiler.common.token.Token;

public class MatchedResult implements Comparable<MatchedResult> {
    public enum MatchType {
        MATCHED,
        MATCHING,
        PARTIAL_MATCH,
        NO_MATCH,
    }

    private final Token token;
    private final MatchType matchType;
    private final int matchedLength;

    public MatchedResult(Token token, MatchType matchType, int matchedLength) {
        this.token = token;
        this.matchType = matchType;
        this.matchedLength = matchedLength;
    }

    @Override
    public int compareTo(MatchedResult o) {
        var compareRez = matchedLength - o.matchedLength;

        if (compareRez != 0) {
            return compareRez;
        }

        if (token instanceof IdentifierToken && o.token instanceof IdentifierToken) {
            return 0;
        }
        if (token instanceof IdentifierToken) {
            return -1;
        }
        if (o.token instanceof IdentifierToken) {
            return 1;
        }

        return 0;
    }

    public boolean stillMatching() {
        return this.matchType == MatchType.MATCHING || this.matchType == MatchType.PARTIAL_MATCH;
    }

    public boolean hasMatched() {
        return this.matchType == MatchType.MATCHED;
    }

    public Token getToken() {
        return token;
    }

    public int getMatchedLength() {
        return matchedLength;
    }
}
