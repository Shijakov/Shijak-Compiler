package com.company.compiler.parser.exceptions;

import com.company.compiler.common.exceptions.LineException;
import com.company.compiler.common.token.Token;

public class UnexpectedTokenException extends LineException {
    public UnexpectedTokenException(Token token, Integer line) {
        super("Unexpected token " + token, line);
    }
}
