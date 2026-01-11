package com.company.old.lexer;

import com.company.old.model.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyMatcher {
    Matcher matcher;
    boolean didMatch;
    int matchedCharsBefore;

    public enum State {
        MATCHED,
        MATCHED_BEFORE,
        PARTIAL_MATCH,
        NEVER_MATCHED,
    }

    public MyMatcher(String regex) {
        Pattern pattern = Pattern.compile(regex);
        this.matcher = pattern.matcher("");
        didMatch = false;
        matchedCharsBefore = 0;
    }
    public Pair<State, Integer> check(String word) {
        matcher.reset(word);
        if (matcher.matches()) {
            didMatch = true;
            matchedCharsBefore = 0;
            return new Pair<>(State.MATCHED, 0);
        } else if (matcher.hitEnd()) {
            if (didMatch) {
                matchedCharsBefore += 1;
            }
            return new Pair<>(State.PARTIAL_MATCH, 0);
        } else {
            if (didMatch) {
                matchedCharsBefore += 1;
                return new Pair<>(State.MATCHED_BEFORE, matchedCharsBefore);
            } else {
                return new Pair<>(State.NEVER_MATCHED, 0);
            }
        }
    }

    public void reset() {
        this.didMatch = false;
        this.matchedCharsBefore = 0;
    }

    @Override
    public String toString() {
        return matcher.pattern().pattern();
    }
}
