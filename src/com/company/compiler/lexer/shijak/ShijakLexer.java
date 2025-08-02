package com.company.compiler.lexer.shijak;

import com.company.compiler.lexer.Lexer;
import com.company.compiler.lexer.TokenInterface;

import java.util.List;

public class ShijakLexer extends Lexer {
    @Override
    protected List<? extends TokenInterface> getTokens() {
        return List.of(ShijakToken.values());
    }
}
