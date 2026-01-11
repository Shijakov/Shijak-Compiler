package com.company.old.lexer;

public class Token {
    public enum TokenType {
        // Keywords
        RETURN_KEYWORD,
        VOID_KEYWORD,
        BOOL_KEYWORD,
        FLOAT_KEYWORD,
        INT_KEYWORD,
        CHAR_KEYWORD,
        FUNCTION_KEYWORD,
        IF_KEYWORD,
        ELIF_KEYWORD,
        ELSE_KEYWORD,
        WHILE_KEYWORD,
        BREAK_KEYWORD,
        CONTINUE_KEYWORD,
        OUTPUT_KEYWORD,
        INPUT_KEYWORD,
        LET_KEYWORD,
        ALLOC_KEYWORD,
        FREE_KEYWORD,
        EQ_KEYWORD,
        IN_KEYWORD,
        DEFINE_KEYWORD,
        BAG_KEYWORD,
        FILL_KEYWORD,
        // Operators
        GREATER_OPERATOR,
        LESS_OPERATOR,
        GREATER_EQUAL_OPERATOR,
        LESS_EQUAL_OPERATOR,
        EQUAL_OPERATOR,
        NOT_EQUAL_OPERATOR,
        OR_OPERATOR,
        AND_OPERATOR,
        NOT_OPERATOR,
        ADDITION_OPERATOR,
        SUBTRACTION_OPERATOR,
        MULTIPLICATION_OPERATOR,
        DIVISION_OPERATOR,
        MODULO_OPERATOR,
        EXPRESSION_OPERATOR,
        // Punctuations
        OPEN_BRACKET,
        CLOSE_BRACKET,
        OPEN_SQUARE_BRACKET,
        CLOSE_SQUARE_BRACKET,
        OPEN_CURLY_BRACKET,
        CLOSE_CURLY_BRACKET,
        COMMA,
        SEMI_COLON,
        COLON,
        DOT,
        // Identifier
        IDENTIFIER,
        // Constant
        INT_CONSTANT,
        FLOAT_CONSTANT,
        BOOL_CONSTANT,
        CHAR_CONSTANT,
        //TERMINAL
        TERMINAL
    }

    TokenType tokenType;
    String value;
    public int line;

    public Token(TokenType tokenType, String value, int line) {
        this.tokenType = tokenType;
        this.value = value;
        this.line = line;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Token( " + tokenType +
                ", " + value +
                ", " + line + ")";
    }
}
