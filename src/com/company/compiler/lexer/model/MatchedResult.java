package com.company.compiler.lexer.model;

public class MatchedResult implements Comparable<MatchedResult> {
    public enum MatchType {
        MATCHED,
        MATCHING,
        PARTIAL_MATCH,
        NO_MATCH,
    }

    private final LexerToken token;
    private final MatchType matchType;
    private final int matchedLength;

    public MatchedResult(LexerToken token, MatchType matchType, int matchedLength) {
        this.token = token;
        this.matchType = matchType;
        this.matchedLength = matchedLength;
    }

    @Override
    public int compareTo(MatchedResult o) {
        var compareRez = matchedLength - o.matchedLength;

        return compareRez != 0 ? compareRez : token.compareTo(o.token);
    }

    public boolean stillMatching() {
        return this.matchType == MatchType.MATCHING || this.matchType == MatchType.PARTIAL_MATCH;
    }

    public boolean hasMatched() {
        return this.matchType == MatchType.MATCHED;
    }

    public LexerToken getToken() {
        return token;
    }

    public int getMatchedLength() {
        return matchedLength;
    }
}
