package com.company.parser.parse_tree;

public enum NodeType {
    S,
    PROGRAM,
    FUNCTION_TAIL,
    DEFINITION,
    DEFINITION_ASSIGNMENT,
    FUNCTION,
    PARAM_LIST,
    PARAM_LIST_TAIL,
    STATEMENT_LIST,
    STATEMENT,
    DEFINE_VAR,
    ALLOC_ARR,
    FREE_ARR,
    RETURN_STATEMENT,
    INPUT_STATEMENT,
    OUTPUT_STATEMENT,
    WHILE_STATEMENT,
    BREAK_STATEMENT,
    CONTINUE_STATEMENT,
    IF_STATEMENT,
    ELIF_STATEMENT,
    ELSE_STATEMENT,
    EXPRESSION_LIST,
    EXPRESSION_LIST_TAIL,
    EXPRESSION_OR_CLOSER,
    CLOSER,
    EXPRESSION,
    EXPRESSION_TAIL,
    MOD_EXPR,
    MOD_EXPR_TAIL,
    ADD_EXPR,
    ADD_EXPR_TAIL,
    MUL_EXPR,
    MUL_EXPR_TAIL,
    PRIMARY,
    INSTANCE,
    INSTANCE_TAIL,
    EXPR_LIST,
    EXPR_LIST_TAIL,
    RETURN_TYPE,
    ALLOC_ARR_TYPE,
    TYPE,
    ARRAY_EXTENSION,
    ARRAY_EMPTY_EXTENSION,
    PRIMITIVE_TYPE,
    PRIMITIVE_CONSTANT
}