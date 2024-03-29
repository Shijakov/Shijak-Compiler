package com.company.exceptions;

import com.company.lexer.Token;
import com.company.parser.parse_tree.ParseTree;

public class UnexpectedTokenException extends Exception{
    public UnexpectedTokenException(Token token, ParseTree.NonTerminalNode node) {
        super(String.format("Unexpected token %s, node was %s", token.toString(), node.nodeType));
    }

    public UnexpectedTokenException(Token token, ParseTree.TerminalNode node) {
        super(String.format("Unexpected token %s, node was %s", token.toString(), node.value));
    }
}
