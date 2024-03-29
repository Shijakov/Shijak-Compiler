package com.company.parser.parse_tree;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.company.lexer.Token.TokenType;
import com.company.parser.parse_tree.ParseTree.Node;
import com.company.parser.parse_tree.ParseTree.NonTerminalNode;
import com.company.parser.parse_tree.ParseTree.TerminalNode;

public class StringToNodeMapper{

    Map<String, Supplier<Node>> map;

    public StringToNodeMapper() {
        this.map = new HashMap<>();

        this.map.put("S", () -> new NonTerminalNode(NodeType.S));
        this.map.put("PROGRAM", () -> new NonTerminalNode(NodeType.PROGRAM));
        this.map.put("FUNCTION_TAIL", () -> new NonTerminalNode(NodeType.FUNCTION_TAIL));
        this.map.put("DEFINITION", () -> new NonTerminalNode(NodeType.DEFINITION));
        this.map.put("DEFINITION_ASSIGNMENT", () -> new NonTerminalNode(NodeType.DEFINITION_ASSIGNMENT));
        this.map.put("FUNCTION", () -> new NonTerminalNode(NodeType.FUNCTION));
        this.map.put("PARAM_LIST", () -> new NonTerminalNode(NodeType.PARAM_LIST));
        this.map.put("PARAM_LIST_TAIL", () -> new NonTerminalNode(NodeType.PARAM_LIST_TAIL));
        this.map.put("STATEMENT_LIST", () -> new NonTerminalNode(NodeType.STATEMENT_LIST));
        this.map.put("STATEMENT", () -> new NonTerminalNode(NodeType.STATEMENT));
        this.map.put("DEFINE_VAR", () -> new NonTerminalNode(NodeType.DEFINE_VAR));
        this.map.put("ALLOC_ARR", () -> new NonTerminalNode(NodeType.ALLOC_ARR));
        this.map.put("FREE_ARR", () -> new NonTerminalNode(NodeType.FREE_ARR));
        this.map.put("RETURN_STATEMENT", () -> new NonTerminalNode(NodeType.RETURN_STATEMENT));
        this.map.put("BREAK_STATEMENT", () -> new NonTerminalNode(NodeType.BREAK_STATEMENT));
        this.map.put("CONTINUE_STATEMENT", () -> new NonTerminalNode(NodeType.CONTINUE_STATEMENT));
        this.map.put("INPUT_STATEMENT", () -> new NonTerminalNode(NodeType.INPUT_STATEMENT));
        this.map.put("OUTPUT_STATEMENT", () -> new NonTerminalNode(NodeType.OUTPUT_STATEMENT));
        this.map.put("WHILE_STATEMENT", () -> new NonTerminalNode(NodeType.WHILE_STATEMENT));
        this.map.put("IF_STATEMENT", () -> new NonTerminalNode(NodeType.IF_STATEMENT));
        this.map.put("ELIF_STATEMENT", () -> new NonTerminalNode(NodeType.ELIF_STATEMENT));
        this.map.put("ELSE_STATEMENT", () -> new NonTerminalNode(NodeType.ELSE_STATEMENT));
        this.map.put("EXPRESSION_LIST", () -> new NonTerminalNode(NodeType.EXPRESSION_LIST));
        this.map.put("EXPRESSION_LIST_TAIL", () -> new NonTerminalNode(NodeType.EXPRESSION_LIST_TAIL));
        this.map.put("EXPRESSION_OR_CLOSER", () -> new NonTerminalNode(NodeType.EXPRESSION_OR_CLOSER));
        this.map.put("CLOSER", () -> new NonTerminalNode(NodeType.CLOSER));
        this.map.put("EXPRESSION", () -> new NonTerminalNode(NodeType.EXPRESSION));
        this.map.put("EXPRESSION_TAIL", () -> new NonTerminalNode(NodeType.EXPRESSION_TAIL));
        this.map.put("MOD_EXPR", () -> new NonTerminalNode(NodeType.MOD_EXPR));
        this.map.put("MOD_EXPR_TAIL", () -> new NonTerminalNode(NodeType.MOD_EXPR_TAIL));
        this.map.put("ADD_EXPR", () -> new NonTerminalNode(NodeType.ADD_EXPR));
        this.map.put("ADD_EXPR_TAIL", () -> new NonTerminalNode(NodeType.ADD_EXPR_TAIL));
        this.map.put("MUL_EXPR", () -> new NonTerminalNode(NodeType.MUL_EXPR));
        this.map.put("MUL_EXPR_TAIL", () -> new NonTerminalNode(NodeType.MUL_EXPR_TAIL));
        this.map.put("PRIMARY", () -> new NonTerminalNode(NodeType.PRIMARY));
        this.map.put("INSTANCE", () -> new NonTerminalNode(NodeType.INSTANCE));
        this.map.put("INSTANCE_TAIL", () -> new NonTerminalNode(NodeType.INSTANCE_TAIL));
        this.map.put("EXPR_LIST", () -> new NonTerminalNode(NodeType.EXPR_LIST));
        this.map.put("EXPR_LIST_TAIL", () -> new NonTerminalNode(NodeType.EXPR_LIST_TAIL));
        this.map.put("RETURN_TYPE", () -> new NonTerminalNode(NodeType.RETURN_TYPE));
        this.map.put("ALLOC_ARR_TYPE", () -> new NonTerminalNode(NodeType.ALLOC_ARR_TYPE));
        this.map.put("TYPE", () -> new NonTerminalNode(NodeType.TYPE));
        this.map.put("ARRAY_EXTENSION", () -> new NonTerminalNode(NodeType.ARRAY_EXTENSION));
        this.map.put("ARRAY_EMPTY_EXTENSION", () -> new NonTerminalNode(NodeType.ARRAY_EMPTY_EXTENSION));
        this.map.put("PRIMITIVE_TYPE", () -> new NonTerminalNode(NodeType.PRIMITIVE_TYPE));
        this.map.put("PRIMITIVE_CONSTANT", () -> new NonTerminalNode(NodeType.PRIMITIVE_CONSTANT));

        this.map.put("$", () -> new TerminalNode(TokenType.TERMINAL, "$"));
        this.map.put("define", () -> new TerminalNode(TokenType.DEFINE_KEYWORD, "define"));
        this.map.put("{", () -> new TerminalNode(TokenType.OPEN_CURLY_BRACKET, "{"));
        this.map.put("}", () -> new TerminalNode(TokenType.CLOSE_CURLY_BRACKET, "}"));
        this.map.put(">>", () -> new TerminalNode(TokenType.EXPRESSION_OPERATOR, ">>"));
        this.map.put("identifier", () -> new TerminalNode(TokenType.IDENTIFIER, ""));
        this.map.put(";", () -> new TerminalNode(TokenType.SEMI_COLON, ";"));
        this.map.put("fun", () -> new TerminalNode(TokenType.FUNCTION_KEYWORD, "fun"));
        this.map.put("(", () -> new TerminalNode(TokenType.OPEN_BRACKET, "("));
        this.map.put(")", () -> new TerminalNode(TokenType.CLOSE_BRACKET, ")"));
        this.map.put(":", () -> new TerminalNode(TokenType.COLON, ":"));
        this.map.put(",", () -> new TerminalNode(TokenType.COMMA, ","));
        this.map.put("let", () -> new TerminalNode(TokenType.LET_KEYWORD, "let"));
        this.map.put("alloc", () -> new TerminalNode(TokenType.ALLOC_KEYWORD, "alloc"));
        this.map.put("free", () -> new TerminalNode(TokenType.FREE_KEYWORD, "free"));
        this.map.put("input", () -> new TerminalNode(TokenType.INPUT_KEYWORD, "input"));
        this.map.put("output", () -> new TerminalNode(TokenType.OUTPUT_KEYWORD, "output"));
        this.map.put("while", () -> new TerminalNode(TokenType.WHILE_KEYWORD, "while"));
        this.map.put("break", () -> new TerminalNode(TokenType.BREAK_KEYWORD, "break"));
        this.map.put("continue", () -> new TerminalNode(TokenType.CONTINUE_KEYWORD, "continue"));
        this.map.put("if", () -> new TerminalNode(TokenType.IF_KEYWORD, "if"));
        this.map.put("elif", () -> new TerminalNode(TokenType.ELIF_KEYWORD, "elif"));
        this.map.put("else", () -> new TerminalNode(TokenType.ELSE_KEYWORD, "else"));
        this.map.put("return", () -> new TerminalNode(TokenType.RETURN_KEYWORD, "return"));
        this.map.put("eq", () -> new TerminalNode(TokenType.EQ_KEYWORD, "eq"));
        this.map.put(">=", () -> new TerminalNode(TokenType.GREATER_EQUAL_OPERATOR, ">="));
        this.map.put("<=", () -> new TerminalNode(TokenType.LESS_EQUAL_OPERATOR, "<="));
        this.map.put("<", () -> new TerminalNode(TokenType.LESS_OPERATOR, "<"));
        this.map.put(">", () -> new TerminalNode(TokenType.GREATER_OPERATOR, ">"));
        this.map.put("==", () -> new TerminalNode(TokenType.EQUAL_OPERATOR, "=="));
        this.map.put("!=", () -> new TerminalNode(TokenType.NOT_EQUAL_OPERATOR, "!="));
        this.map.put("%", () -> new TerminalNode(TokenType.MODULO_OPERATOR, "%"));
        this.map.put("+", () -> new TerminalNode(TokenType.ADDITION_OPERATOR, "+"));
        this.map.put("-", () -> new TerminalNode(TokenType.SUBTRACTION_OPERATOR, "-"));
        this.map.put("||", () -> new TerminalNode(TokenType.OR_OPERATOR, "||"));
        this.map.put("*", () -> new TerminalNode(TokenType.MULTIPLICATION_OPERATOR, "*"));
        this.map.put("/", () -> new TerminalNode(TokenType.DIVISION_OPERATOR, "/"));
        this.map.put("&&", () -> new TerminalNode(TokenType.AND_OPERATOR, "&&"));
        this.map.put("!", () -> new TerminalNode(TokenType.NOT_OPERATOR, "!"));
        this.map.put("in", () -> new TerminalNode(TokenType.IN_KEYWORD, "in"));
        this.map.put("void", () -> new TerminalNode(TokenType.VOID_KEYWORD, "void"));
        this.map.put("[", () -> new TerminalNode(TokenType.OPEN_SQUARE_BRACKET, "["));
        this.map.put("]", () -> new TerminalNode(TokenType.CLOSE_SQUARE_BRACKET, "]"));
        this.map.put("float", () -> new TerminalNode(TokenType.FLOAT_KEYWORD, "float"));
        this.map.put("int", () -> new TerminalNode(TokenType.INT_KEYWORD, "int"));
        this.map.put("char", () -> new TerminalNode(TokenType.CHAR_KEYWORD, "char"));
        this.map.put("bool", () -> new TerminalNode(TokenType.BOOL_KEYWORD, "bool"));
        this.map.put("char_constant", () -> new TerminalNode(TokenType.CHAR_CONSTANT, ""));
        this.map.put("bool_constant", () -> new TerminalNode(TokenType.BOOL_CONSTANT, ""));
        this.map.put("int_constant", () -> new TerminalNode(TokenType.INT_CONSTANT, ""));
        this.map.put("float_constant", () -> new TerminalNode(TokenType.FLOAT_CONSTANT, ""));
    }

    public Node getNode(String nodeAsString) {
        return this.map.get(nodeAsString).get();
    }
}
