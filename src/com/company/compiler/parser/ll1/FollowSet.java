package com.company.compiler.parser.ll1;

import com.company.compiler.common.exceptions.DevException;
import com.company.compiler.common.grammar.Grammar;
import com.company.compiler.common.grammar.Rule;
import com.company.compiler.common.symbol.NonTerminal;
import com.company.compiler.common.symbol.StartSymbol;
import com.company.compiler.common.symbol.Terminal;
import com.company.compiler.common.token.TerminalToken;
import com.company.compiler.shijak.ShijakNonTerminals;

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
        this.entries.compute(nonTerminal, (k, v) -> v == null ? new HashSet<>() : v).addAll(terminals);
    }

    public boolean hasFor(NonTerminal nonTerminal) {
        return this.entries.containsKey(nonTerminal);
    }

    public Set<Terminal> getFor(NonTerminal nonTerminal) {
        return new HashSet<>(this.entries.getOrDefault(nonTerminal, Set.of()));
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

        while(!unions.isEmpty()) {
            var changeOccurred = false;
            for (var nonTerminal : unions.keySet()) {
                var rez = unions.get(nonTerminal).compute(nonTerminal, firstSet, followSet);
                var elmsBefore = followSet.getFor(nonTerminal).size();
                followSet.setFor(nonTerminal, rez);
                var elmsAfter = followSet.getFor(nonTerminal).size();
                if (elmsBefore != elmsAfter) {
                    changeOccurred = true;
                }
            }
            if (!changeOccurred) {
                break;
            }
        }

        return followSet;
    }
}
