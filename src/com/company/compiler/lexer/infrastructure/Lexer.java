package com.company.compiler.lexer.infrastructure;

import com.company.compiler.common.token.RecognisedToken;
import com.company.compiler.common.token.Token;
import com.company.compiler.lexer.exceptions.TokenNotRecognizedException;

import java.util.List;

public interface Lexer {
    List<RecognisedToken> analyze(String program, List<Token> definedTokens) throws TokenNotRecognizedException;
}
