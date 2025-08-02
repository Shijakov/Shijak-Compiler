package com.company.compiler.lexer;

public enum AutomatonState {
    MATCHED,
    MATCHED_BEFORE,
    PARTIAL_MATCH,
    NEVER_MATCHED,
}
