package com.company.compiler.lexer.shijak;

import com.company.compiler.lexer.TokenInterface;

public enum ShijakToken implements TokenInterface {
    RETURN_KEYWORD("return"),
    VOID_KEYWORD("void"),
    BOOL_KEYWORD("bool"),
    FLOAT_KEYWORD("float"),
    INT_KEYWORD("int"),
    CHAR_KEYWORD("char"),
    FUNCTION_KEYWORD("fun"),
    IF_KEYWORD("if"),
    ELIF_KEYWORD("elif"),
    ELSE_KEYWORD("else"),
    WHILE_KEYWORD("while"),
    BREAK_KEYWORD("break"),
    CONTINUE_KEYWORD("continue"),
    OUTPUT_KEYWORD("output"),
    INPUT_KEYWORD("input"),
    LET_KEYWORD("let"),
    ALLOC_KEYWORD("alloc"),
    FREE_KEYWORD("free"),
    EQ_KEYWORD("eq"),
    IN_KEYWORD("in"),
    DEFINE_KEYWORD("define"),
    BAG_KEYWORD("bag"),
    FILL_KEYWORD("fill"),
    // Operators
    GREATER_OPERATOR(">"),
    LESS_OPERATOR("<"),
    GREATER_EQUAL_OPERATOR(">="),
    LESS_EQUAL_OPERATOR("<="),
    EQUAL_OPERATOR("=="),
    NOT_EQUAL_OPERATOR("!="),
    OR_OPERATOR("\\|\\|"),
    AND_OPERATOR("&&"),
    NOT_OPERATOR("!"),
    ADDITION_OPERATOR("\\+"),
    SUBTRACTION_OPERATOR("-"),
    MULTIPLICATION_OPERATOR("\\*"),
    DIVISION_OPERATOR("/"),
    MODULO_OPERATOR("%"),
    EXPRESSION_OPERATOR(">>"),
    // Punctuations
    OPEN_BRACKET("\\("),
    CLOSE_BRACKET("\\)"),
    OPEN_SQUARE_BRACKET("\\["),
    CLOSE_SQUARE_BRACKET("]"),
    OPEN_CURLY_BRACKET("\\{"),
    CLOSE_CURLY_BRACKET("}"),
    COMMA(","),
    SEMI_COLON(";"),
    COLON(":"),
    DOT("\\."),
    // Identifier
    IDENTIFIER("[a-zA-Z_]+[a-zA-Z_0-9]*"),
    // Constant
    INT_CONSTANT("-?[0-9]+"),
    FLOAT_CONSTANT("-?[0-9]+\\.[0-9]+"),
    BOOL_CONSTANT("true|false"),
    CHAR_CONSTANT("'.'"),
    // BLANK
    BLANK_SPACE("[ \t]*", true, false),
    COMMENT("//.*\n", true, false),
    MULTI_LINE_COMMENT("/\\*[^(\\*/)]*\\*/", true, false),
    NEW_LINE("\n", true, true),
    //TERMINAL
    TERMINAL(null);

    private final String regex;
    private final boolean isBlank;
    private final boolean isNewLine;

    ShijakToken(String regex, boolean isBlank, boolean isNewLine) {
        this.regex = regex;
        this.isBlank = isBlank;
        this.isNewLine = isNewLine;
    }

    ShijakToken(String regex) {
        this.regex = regex;
        this.isBlank = false;
        this.isNewLine = false;
    }

    @Override
    public String getRegex() {
        return this.regex;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public boolean isBlank() {
        return this.isBlank;
    }

    @Override
    public boolean isNewLine() {
        return this.isNewLine;
    }
}
