package com.company.compiler.common.grammar;

import com.company.compiler.common.grammar.exceptions.GrammarDefinedWithNoStartSymbol;
import com.company.compiler.common.symbol.NonTerminal;
import com.company.compiler.common.symbol.StartSymbol;

import java.util.*;

public class Grammar {
    Map<NonTerminal, Set<Rule>> rules;

    private Grammar(Collection<Rule> rules) {
        this.rules = new HashMap<>();
        for (var rule : rules) {
            this.rules.compute(rule.left, (k, v) -> v == null ? new HashSet<>() : v).add(rule);
        }
    }

    private static boolean hasStartSymbol(Collection<Rule> rules) {
        return rules.stream().anyMatch((rule) -> rule.left.equals(new StartSymbol()));
    }

    static Grammar from(Collection<Rule> rules) {
        if (!hasStartSymbol(rules)) {
            throw new GrammarDefinedWithNoStartSymbol();
        }

        return new Grammar(rules);
    }

    public Collection<Rule> getRulesFor(NonTerminal nonTerminal) {
        return this.rules.get(nonTerminal);
    }

    public Collection<NonTerminal> getNonTerminals() {
        return rules.keySet();
    }

    public Collection<Rule> getRules() {
        return rules.values().stream().flatMap(Collection::stream).toList();
    }
}
