package com.company.compiler.common.grammar;

import com.company.compiler.common.symbol.EmptySymbol;
import com.company.compiler.common.symbol.NonTerminal;
import com.company.compiler.common.symbol.Symbol;
import com.company.compiler.common.token.TerminalToken;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Rule {
    NonTerminal left;
    List<Symbol> right;

    public Rule(NonTerminal left, List<Symbol> right) {
        this.left = left;
        this.right = right;
    }

    public NonTerminal getLeft() {
        return left;
    }

    public List<Symbol> getRight() {
        return right;
    }

    public boolean isEmpty() {
        return right.size() == 1 && Objects.equals(right.getFirst(), new EmptySymbol());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Rule rule = (Rule) object;
        return Objects.equals(left, rule.left) && Objects.equals(right, rule.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
