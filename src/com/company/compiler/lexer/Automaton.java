package com.company.compiler.lexer;

import com.company.model.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Automaton implements AutomatonInterface {
    TokenInterface token;
    StringBuilder word;
    Matcher matcher;
    boolean didMatch;
    int matchedSignsBefore;

    public Automaton(TokenInterface token) {
        this.matcher = (Pattern.compile(token.getRegex())).matcher("");
        this.token = token;
        this.reset();
    }

    @Override
    public Pair<AutomatonState, Integer> feed(Character sign) {
        this.word.append(sign);
        return this.check();
    }

    @Override
    public void reset() {
        this.word.setLength(0);
        this.didMatch = false;
        this.matchedSignsBefore = 0;
    }

    @Override
    public TokenInterface getToken() {
        return this.token;
    }

    @Override
    public int getLengthOfRecognizedWord() {
        if (didMatch) {
            return this.word.length() - this.matchedSignsBefore;
        }
        return 0;
    }

    @Override
    public String getWord() {
        return this.word.toString();
    }

    @Override
    public String getMatchedWord() {
        return this.word.substring(0, this.word.length() - matchedSignsBefore);
    }

    @Override
    public int getMatchedSignsBefore() {
        return this.matchedSignsBefore;
    }

    @Override
    public String toString() {
        return matcher.pattern().pattern();
    }

    private Pair<AutomatonState, Integer> check() {
        matcher.reset(word);
        if (matcher.matches()) {
            didMatch = true;
            matchedSignsBefore = 0;
            return new Pair<>(AutomatonState.MATCHED, 0);
        } else if (matcher.hitEnd()) {
            if (didMatch) {
                matchedSignsBefore += 1;
            }
            return new Pair<>(AutomatonState.PARTIAL_MATCH, 0);
        } else {
            if (didMatch) {
                matchedSignsBefore += 1;
                return new Pair<>(AutomatonState.MATCHED_BEFORE, matchedSignsBefore);
            } else {
                return new Pair<>(AutomatonState.NEVER_MATCHED, 0);
            }
        }
    }
}
