package com.company.exceptions;

import com.company.lexer.Token;
import com.company.parser.parse_tree.ParseTree;

public class UnexpectedTokenException extends LineInfoException {
    public UnexpectedTokenException(Token token, ParseTree.NonTerminalNode node, int line) {
        super(String.format("Unexpected token %s, node was %s", token.toString(), node.nodeType), line);
    }

    public UnexpectedTokenException(Token token, ParseTree.TerminalNode node, int line) {
        super(String.format("Unexpected token %s, node was %s", token.toString(), node.value), line);
    }
}
