package com.company.compiler.helpers;

import com.company.compiler.common.grammar.Rule;
import com.company.compiler.common.symbol.NonTerminal;
import com.company.compiler.common.symbol.Symbol;
import com.company.compiler.common.symbol.Terminal;
import com.company.compiler.common.token.IdentifierToken;
import com.company.compiler.common.token.IgnoredToken;
import com.company.compiler.common.token.Token;
import com.company.compiler.parser.model.ParseNode;

import java.util.List;

public class Helpers {
    public static Token token(String regex) {
        return new Token(regex);
    }

    public static IdentifierToken identifierToken(String regex) {
        return new IdentifierToken(regex);
    }

    public static IgnoredToken ignoredToken(String regex) {
        return new IgnoredToken(regex);
    }

    public static NonTerminal nonTerminal(String name) {
        return new NonTerminal(name);
    }

    public static Terminal terminal(String name) {
        return new Terminal(name);
    }

    public static Rule rule(NonTerminal nonTerminal, List<Symbol> right) {
        return new Rule(nonTerminal, right);
    }

    public static ParseNode node(Symbol value) {
        return new ParseNode(value);
    }
}
