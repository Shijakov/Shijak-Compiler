package com.company.compiler.parser.ll1;

import com.company.compiler.common.grammar.GrammarBuilder;
import com.company.compiler.common.symbol.EmptySymbol;
import com.company.compiler.common.symbol.StartSymbol;
import com.company.compiler.common.token.TerminalToken;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static com.company.compiler.helpers.Helpers.*;

public class LL1TableTest {
    @Test
    public void testClassicExpressionGrammar() {
        var S = new StartSymbol();
        var E = nonTerminal("E");
        var Ep = nonTerminal("Ep");
        var T = nonTerminal("T");
        var Tp = nonTerminal("Tp");
        var F = nonTerminal("F");
        var plus = terminal("+");
        var times = terminal("*");
        var open = terminal("(");
        var close = terminal(")");
        var id = terminal("id");
        var tt = new TerminalToken();

        var grammar = GrammarBuilder.grammar(E)
                .withAdded(rule(E, List.of(T, Ep)))
                .withAdded(rule(Ep, List.of(plus, T, Ep)))
                .withAdded(rule(Ep, List.of(new EmptySymbol())))
                .withAdded(rule(T, List.of(F, Tp)))
                .withAdded(rule(Tp, List.of(times, F, Tp)))
                .withAdded(rule(Tp, List.of(new EmptySymbol())))
                .withAdded(rule(F, List.of(open, E, close)))
                .withAdded(rule(F, List.of(id)))
                .build();

        var firstSet = FirstSet.from(grammar);

        var followSet = FollowSet.from(grammar, firstSet);

        var table = LL1Table.from(grammar, firstSet, followSet);

        assertEquals(15, table.size());

        assertEquals(
                rule(S, List.of(E, tt)),
                table.getRule(S, open)
        );

        assertEquals(
                rule(S, List.of(E, tt)),
                table.getRule(S, id)
        );

        assertEquals(
                rule(E, List.of(T, Ep)),
                table.getRule(E, open)
        );

        assertEquals(
                rule(E, List.of(T, Ep)),
                table.getRule(E, id)
        );

        assertEquals(
                rule(Ep, List.of(new EmptySymbol())),
                table.getRule(Ep, tt)
        );

        assertEquals(
                rule(Ep, List.of(plus, T, Ep)),
                table.getRule(Ep, plus)
        );

        assertEquals(
                rule(Ep, List.of(new EmptySymbol())),
                table.getRule(Ep, close)
        );

        assertEquals(
                rule(T, List.of(F, Tp)),
                table.getRule(T, open)
        );

        assertEquals(
                rule(T, List.of(F, Tp)),
                table.getRule(T, id)
        );


        assertEquals(
                rule(Tp, List.of(new EmptySymbol())),
                table.getRule(Tp, tt)
        );

        assertEquals(
                rule(Tp, List.of(new EmptySymbol())),
                table.getRule(Tp, plus)
        );

        assertEquals(
                rule(Tp, List.of(times, F, Tp)),
                table.getRule(Tp, times)
        );

        assertEquals(
                rule(Tp, List.of(new EmptySymbol())),
                table.getRule(Tp, close)
        );

        assertEquals(
                rule(F, List.of(open, E, close)),
                table.getRule(F, open)
        );

        assertEquals(
                rule(F, List.of(id)),
                table.getRule(F, id)
        );
    }
}
