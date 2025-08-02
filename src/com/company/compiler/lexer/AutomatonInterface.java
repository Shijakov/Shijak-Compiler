package com.company.compiler.lexer;

import com.company.model.Pair;

public interface AutomatonInterface {
    void reset();
    Pair<AutomatonState, Integer> feed(Character sign);
    TokenInterface getToken();
    int getLengthOfRecognizedWord();
    String getWord();
    String getMatchedWord();
    int getMatchedSignsBefore();
}
