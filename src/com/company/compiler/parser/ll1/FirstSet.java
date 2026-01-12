package com.company.compiler.parser.ll1;

import com.company.compiler.common.exceptions.DevException;
import com.company.compiler.common.grammar.Grammar;
import com.company.compiler.common.grammar.Rule;
import com.company.compiler.common.symbol.EmptySymbol;
import com.company.compiler.common.symbol.NonTerminal;
import com.company.compiler.common.symbol.Symbol;
import com.company.compiler.common.symbol.Terminal;
import com.company.compiler.parser.ll1.exceptions.GrammarIsLeftRecursive;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FirstSet {
    Map<NonTerminal, Set<Terminal>> entries;
    Map<Rule, Set<Terminal>> ruleEntries;

    private FirstSet() {
        this.entries = new HashMap<>();
        this.ruleEntries = new HashMap<>();
    }

    private void setFor(NonTerminal nonTerminal, Set<Terminal> terminals) {
        this.entries.put(nonTerminal, terminals);
    }

    private void setFor(Rule rule, Set<Terminal> terminals) {
        this.ruleEntries.put(rule, terminals);
    }

    public boolean hasFor(Symbol symbol) {
        if (symbol instanceof Terminal) return true;
        return this.entries.containsKey((NonTerminal) symbol);
    }

    public Set<Terminal> getFor(Symbol symbol) {
        if (symbol instanceof Terminal) return new HashSet<>(Set.of((Terminal) symbol));
        return new HashSet<>(this.entries.get((NonTerminal) symbol));
    }

    public Set<Terminal> getFor(Rule rule) {
        if (!this.ruleEntries.containsKey(rule)) {
            throw new DevException("FirstSet doesn't have rule key");
        }
        return new HashSet<>(this.ruleEntries.get(rule));
    }

    public boolean containsFor(Symbol symbol, Terminal terminal) {
        if (symbol.equals(terminal)) return true;
        if (symbol instanceof Terminal) return false;

        return this.entries.getOrDefault((NonTerminal) symbol, new HashSet<>()).contains(terminal);
    }

    public boolean containsEmptyFor(Symbol symbol) {
        if (symbol instanceof EmptySymbol) return true;
        if (symbol instanceof Terminal) return false;

        return containsFor(symbol, new EmptySymbol());
    }

    public int size() {
        return entries.size();
    }

    private static Set<Terminal> firstSet(Rule rule, FirstSet firstSet, Grammar grammar, Set<NonTerminal> visited) {
        var nonTerminalFirstSet = new HashSet<Terminal>();

        var symbolIterator = rule.getRight().iterator();

        while (symbolIterator.hasNext()) {
            var ruleFirstSet = firstSet(symbolIterator.next(), firstSet, grammar, visited);
            nonTerminalFirstSet.addAll(ruleFirstSet);
            if (!ruleFirstSet.contains(new EmptySymbol())) {
                break;
            }
            if (symbolIterator.hasNext()) {
                nonTerminalFirstSet.remove(new EmptySymbol());
            }
        }

        firstSet.setFor(rule, nonTerminalFirstSet);
        return nonTerminalFirstSet;
    }

    private static Set<Terminal> firstSet(Symbol symbol, FirstSet firstSet, Grammar grammar, Set<NonTerminal> visited) {
        if (symbol instanceof Terminal) {
            return Set.of((Terminal) symbol);
        }
        if (firstSet.hasFor(symbol)) {
            return firstSet.getFor(symbol);
        }

        if (visited.contains((NonTerminal) symbol)) {
            throw new GrammarIsLeftRecursive();
        }
        visited.add((NonTerminal) symbol);

        var firstOfNonTerminal = new HashSet<Terminal>();

        for (var rule : grammar.getRulesFor((NonTerminal) symbol)) {
            firstOfNonTerminal.addAll(firstSet(rule, firstSet, grammar, visited));
        }

        firstSet.setFor((NonTerminal) symbol, firstOfNonTerminal);
        return firstOfNonTerminal;
    }

    public static FirstSet from(Grammar grammar) {
        var firstSet = new FirstSet();
        var visited = new HashSet<NonTerminal>();
        for (var nonTerminal : grammar.getNonTerminals()) {
            firstSet(nonTerminal, firstSet, grammar, visited);
        }
        return firstSet;
    }
}
