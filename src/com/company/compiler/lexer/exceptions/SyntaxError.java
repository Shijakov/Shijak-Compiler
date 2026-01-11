package com.company.compiler.lexer.exceptions;

import com.company.compiler.common.exceptions.LineException;

public class SyntaxError extends LineException {
    public SyntaxError(String word, int line) {
        super("Token not recognized " + word, line);
    }
}
