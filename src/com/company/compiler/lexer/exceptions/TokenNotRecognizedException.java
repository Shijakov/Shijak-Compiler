package com.company.compiler.lexer.exceptions;

import com.company.compiler.common.exceptions.LineException;

public class TokenNotRecognizedException extends LineException {
    public TokenNotRecognizedException(String word, int line) {
        super("Token not recognized " + word, line);
    }
}
