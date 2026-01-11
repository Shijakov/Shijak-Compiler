package com.company.compiler.lexer.infrastructure;

import com.company.compiler.common.token.RecognisedToken;
import com.company.compiler.lexer.exceptions.SyntaxError;
import com.company.compiler.lexer.model.LexerToken;

import java.util.List;

public interface Lexer {
    List<RecognisedToken> analyze(String program, List<LexerToken> definedTokens) throws SyntaxError;
}
