package com.company.compiler.lexer.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenMatcher {
    private final LexerToken token;
    private final Matcher matcher;

    public TokenMatcher(LexerToken token) {
        this.token = token;
        Pattern pattern = Pattern.compile(token.getToken().getRegex());
        matcher = pattern.matcher("");
    }

    public MatchedResult matches(String word) {
        matcher.reset(word);

        if (matcher.lookingAt()) {
            if (matcher.end() == word.length()) {
                return new MatchedResult(token, MatchedResult.MatchType.MATCHING, 0);
            }
            return new MatchedResult(token, MatchedResult.MatchType.MATCHED, matcher.end());
        }
        if (matcher.hitEnd()) {
            return new MatchedResult(token, MatchedResult.MatchType.PARTIAL_MATCH, 0);
        }
        return new MatchedResult(token, MatchedResult.MatchType.NO_MATCH, 0);
    }

    public LexerToken getToken() {
        return token;
    }
}

