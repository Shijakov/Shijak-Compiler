package com.company.compiler.lexer;

public interface TokenInterface {
    String getRegex();
    String getName();
    boolean isBlank();
    boolean isNewLine();
}
