package com.company.old.exceptions;

import com.company.old.lexer.Token;
import com.company.old.parser.parse_tree.ParseTree;

public class UnexpectedTokenException extends LineInfoException {
    public UnexpectedTokenException(Token token, ParseTree.NonTerminalNode node, int line) {
        super(String.format("Unexpected token %s, node was %s", token.toString(), node.nodeType), line);
    }

    public UnexpectedTokenException(Token token, ParseTree.TerminalNode node, int line) {
        super(String.format("Unexpected token %s, node was %s", token.toString(), node.value), line);
    }
}
