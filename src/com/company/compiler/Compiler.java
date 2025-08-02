package com.company.compiler;

import com.company.compiler.lexer.Lexer;
import com.company.compiler.lexer.LexerInterface;
import com.company.compiler.lexer.TokenInterface;
import com.company.exceptions.UnknownSymbolException;

import java.util.Collection;

public abstract class Compiler {

    protected abstract Collection<TokenInterface> getTokens();

    public String compile(String program) throws UnknownSymbolException {
        LexerInterface lexer = new Lexer(this.getTokens());
        var tokens = lexer.analyze(program);

        return "";
    }
}
