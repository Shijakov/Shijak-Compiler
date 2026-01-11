package com.company.compiler.parser.ll1;

import com.company.compiler.common.exceptions.DevException;
import com.company.compiler.common.grammar.Grammar;
import com.company.compiler.common.grammar.Rule;
import com.company.compiler.common.symbol.NonTerminal;
import com.company.compiler.common.symbol.StartSymbol;
import com.company.compiler.common.symbol.Terminal;
import com.company.compiler.common.token.TerminalToken;

import java.util.*;

public class FollowSet {
    Map<NonTerminal, Set<Terminal>> entries;

    private FollowSet() {
        this.entries = new HashMap<>();
    }

    public int size() {
        return entries.size();
    }

    private void setFor(NonTerminal nonTerminal, Set<Terminal> terminals) {
        this.entries.put(nonTerminal, terminals);
    }

    public boolean hasFor(NonTerminal nonTerminal) {
        return this.entries.containsKey(nonTerminal);
    }

    public Set<Terminal> getFor(NonTerminal nonTerminal) {
        return new HashSet<>(this.entries.get(nonTerminal));
    }

    private static void computeUnions(Rule rule, FirstSet firstSet, Map<NonTerminal, Union> unions) {
        HashSet<NonTerminal> lookingAtNonTerminals = new HashSet<>();

        for (var symbol : rule.getRight()) {
            for (var nonTerminal : lookingAtNonTerminals) {
                unions
                        .compute(nonTerminal, (k, v) -> v == null ? new Union() : v)
                        .addFirst(symbol);
            }
            if (!firstSet.containsEmptyFor(symbol)) {
                lookingAtNonTerminals = new HashSet<>();
            }
            if (symbol instanceof NonTerminal) {
                lookingAtNonTerminals.add((NonTerminal) symbol);
            }
        }

        for (var nonTerminal : lookingAtNonTerminals) {
            unions
                    .compute(nonTerminal, (k, v) -> v == null ? new Union() : v)
                    .addFollow(rule.getLeft());
        }
    }

    private static void computeUnions(Collection<Rule> rules, FirstSet firstSet, Map<NonTerminal, Union> unions) {
        for (var rule : rules) {
            computeUnions(rule, firstSet, unions);
        }
    }

    public static FollowSet from(Grammar grammar, FirstSet firstSet) {
        Map<NonTerminal, Union> unions = new HashMap<>();
        computeUnions(grammar.getRules(), firstSet, unions);

        var followSet = new FollowSet();
        followSet.setFor(new StartSymbol(), Set.of(new TerminalToken()));

        while(!unions.isEmpty()) {
            var changeOccurred = false;
            var unionsToRemove = new ArrayList<NonTerminal>();
            for (var nonTerminal : unions.keySet()) {
                var rez = unions.get(nonTerminal).compute(nonTerminal, firstSet, followSet);
                if (rez != null) {
                    changeOccurred = true;
                    followSet.setFor(nonTerminal, rez);
                    unionsToRemove.add(nonTerminal);
                }
            }
            for (var toRemove : unionsToRemove) {
                unions.remove(toRemove);
            }
            if (!changeOccurred) {
                throw new DevException("An iteration without change happened while computing follow set");
            }
        }

        return followSet;
    }
}
