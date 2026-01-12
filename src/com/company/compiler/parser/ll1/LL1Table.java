package com.company.compiler.parser.ll1;

import com.company.compiler.common.exceptions.DevException;
import com.company.compiler.common.grammar.Grammar;
import com.company.compiler.common.grammar.Rule;
import com.company.compiler.common.symbol.EmptySymbol;
import com.company.compiler.common.symbol.NonTerminal;
import com.company.compiler.common.symbol.Terminal;
import com.company.compiler.parser.ll1.exceptions.TableEntryAlreadyHasAValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LL1Table {
    private record Key(NonTerminal nonTerminal, Terminal terminal) {
        @Override
        public String toString() {
            return "Key(" + nonTerminal +
                    ", " + terminal + ')';
        }
    }

    private final Map<Key, Rule> table;

    private LL1Table() {
        table = new HashMap<>();
    }

    public Rule getRule(NonTerminal nonTerminal, Terminal terminal) {
        if (!table.containsKey(new Key(nonTerminal, terminal))) {
            throw new DevException("No rule for table entry");
        }
        return table.get(new Key(nonTerminal, terminal));
    }

    public int size() {
        return table.size();
    }

    private void addRule(Key key, Rule rule) {
        if (table.containsKey(key)) {
            throw new TableEntryAlreadyHasAValue();
        }

        table.put(key, rule);
    }

    private static void addEntries(LL1Table table, Rule rule, Set<Terminal> terminals) {
        for (var terminal : terminals) {
            if (terminal.equals(new EmptySymbol())) continue;
            table.addRule(new Key(rule.getLeft(), terminal), rule);
        }
    }

    public static LL1Table from(Grammar grammar, FirstSet firstSet, FollowSet followSet) {
        var table = new LL1Table();
        for (var rule : grammar.getRules()) {
            var firstSetOfRule = firstSet.getFor(rule);
            addEntries(table, rule, firstSetOfRule);

            if (!firstSetOfRule.contains(new EmptySymbol())) {
                continue;
            }

            var followSetOfNonTerminal = followSet.getFor(rule.getLeft());
            addEntries(table, new Rule(rule.getLeft(), List.of(new EmptySymbol())), followSetOfNonTerminal);
        }

        return table;
    }
}
