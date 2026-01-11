package com.company.compiler.common.grammar;

import com.company.compiler.common.symbol.NonTerminal;
import com.company.compiler.common.symbol.Symbol;

import java.util.Iterator;
import java.util.List;

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

    public Iterator<Symbol> ruleIterator() {
        return right.iterator();
    }

    public List<Symbol> getRight() {
        return right;
    }
}
