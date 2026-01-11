package com.company.compiler.parser.ll1;

import com.company.compiler.common.symbol.EmptySymbol;
import com.company.compiler.common.symbol.NonTerminal;
import com.company.compiler.common.symbol.Symbol;
import com.company.compiler.common.symbol.Terminal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Union {
    private final Set<UnionElement> union;

    public interface UnionElement {}

    public record FollowOf(NonTerminal nonTerminal) implements UnionElement {}
    public record FirstOf(Symbol symbol) implements UnionElement {}

    public Union() {
        this.union = new HashSet<>();
    }

    void addFirst(Symbol symbol) {
        this.union.add(new FirstOf(symbol));
    }

    void addFollow(NonTerminal nonTerminal) {
        this.union.add(new FollowOf(nonTerminal));
    }

    private Set<Terminal> getTerminalsFor(UnionElement element, NonTerminal nonTerminal, FirstSet firstSet, FollowSet followSet) {
        if (element instanceof FirstOf) {
            if (!firstSet.hasFor(((FirstOf) element).symbol)) return null;
            var terminals = firstSet.getFor(((FirstOf) element).symbol);
            terminals.remove(new EmptySymbol());
            return terminals;
        }
        if (((FollowOf) element).nonTerminal.equals(nonTerminal)) {
            return Set.of();
        }
        return followSet.hasFor(((FollowOf) element).nonTerminal) ? followSet.getFor(((FollowOf) element).nonTerminal) : null;
    }

    public Set<Terminal> compute(NonTerminal nonTerminal, FirstSet firstSet, FollowSet followSet) {
        Set<Terminal> terminalsSet = new HashSet<>();

        for (var element : union) {
            var terminals = getTerminalsFor(element, nonTerminal, firstSet, followSet);
            if (terminals == null) return null;
            terminalsSet.addAll(terminals);
        }

        return terminalsSet;
    }

    public Set<UnionElement> getUnion() {
        return this.union;
    }
}
