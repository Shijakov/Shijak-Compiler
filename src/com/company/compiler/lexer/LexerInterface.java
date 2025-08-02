package com.company.compiler.lexer;

import com.company.exceptions.UnknownSymbolException;

import java.util.Collection;

public interface LexerInterface {
    Collection<MatchedToken> analyze(String program) throws UnknownSymbolException;
}
