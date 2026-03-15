package com.company.compiler;

import com.company.compiler.common.grammar.Grammar;
import com.company.compiler.lexer.infrastructure.Lexer;
import com.company.compiler.lexer.model.LexerToken;
import com.company.compiler.parser.infrastructure.Parser;

import java.util.List;

public abstract class Compiler {

    protected abstract List<LexerToken> getTokens();

    protected abstract Grammar getGrammar();

    protected abstract Lexer getLexer();

    protected abstract Parser getParser();

    public String compile(String program) {
        var recognisedTokens = this.getLexer().analyze(program, this.getTokens());

        var tree = this.getParser().parse(recognisedTokens, this.getGrammar());

        return "";
    }
}
