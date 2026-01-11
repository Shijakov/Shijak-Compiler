package com.company.compiler.parser.ll1;

import com.company.compiler.common.grammar.GrammarBuilder;
import com.company.compiler.common.symbol.EmptySymbol;
import com.company.compiler.common.symbol.StartSymbol;
import com.company.compiler.common.token.TerminalToken;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static com.company.compiler.helpers.Helpers.*;

public class FollowSetTest {
    @Test
    public void testSingleNonTerminal() {
        var S = new StartSymbol();
        var A = nonTerminal("A");
        var a = terminal("a");
        var b = terminal("b");
        var c = terminal("c");

        var grammar = GrammarBuilder.grammar()
                .withAdded(rule(S, List.of(a, A, b)))
                .withAdded(rule(A, List.of(c)))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(2, followSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(S)
        );

        assertEquals(
                Set.of(b),
                followSet.getFor(A)
        );
    }

    @Test
    public void testEmptyProduction() {
        var S = new StartSymbol();
        var A = nonTerminal("A");
        var b = terminal("b");

        var grammar = GrammarBuilder.grammar()
                .withAdded(rule(S, List.of(A, b)))
                .withAdded(rule(A, List.of(new EmptySymbol())))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(2, followSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(S)
        );

        assertEquals(
                Set.of(b),
                followSet.getFor(A)
        );
    }

    @Test
    public void testNonTerminalAtEnd() {
        var S = new StartSymbol();
        var A = nonTerminal("A");
        var a = terminal("a");
        var b = terminal("b");

        var grammar = GrammarBuilder.grammar()
                .withAdded(rule(S, List.of(a, A)))
                .withAdded(rule(A, List.of(b)))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(2, followSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(S)
        );

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(A)
        );
    }

    @Test
    public void testMultipleOccurrences() {
        var S = new StartSymbol();
        var A = nonTerminal("A");
        var B = nonTerminal("B");
        var C = nonTerminal("C");
        var a = terminal("a");
        var b = terminal("b");
        var c = terminal("c");

        var grammar = GrammarBuilder.grammar()
                .withAdded(rule(S, List.of(A, B, C)))
                .withAdded(rule(A, List.of(a)))
                .withAdded(rule(B, List.of(b)))
                .withAdded(rule(C, List.of(c)))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(4, followSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(S)
        );

        assertEquals(
                Set.of(b),
                followSet.getFor(A)
        );

        assertEquals(
                Set.of(c),
                followSet.getFor(B)
        );

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(C)
        );
    }

    @Test
    public void testEmptyInMiddle() {
        var S = new StartSymbol();
        var A = nonTerminal("A");
        var B = nonTerminal("B");
        var C = nonTerminal("C");
        var a = terminal("a");
        var c = terminal("c");

        var grammar = GrammarBuilder.grammar()
                .withAdded(rule(S, List.of(A, B, C)))
                .withAdded(rule(A, List.of(a)))
                .withAdded(rule(B, List.of(new EmptySymbol())))
                .withAdded(rule(C, List.of(c)))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(4, followSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(S)
        );

        assertEquals(
                Set.of(c),
                followSet.getFor(A)
        );

        assertEquals(
                Set.of(c),
                followSet.getFor(B)
        );

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(C)
        );
    }

    @Test
    public void testRecursiveProduction() {
        var S = new StartSymbol();
        var A = nonTerminal("A");
        var B = nonTerminal("B");
        var a = terminal("a");

        var grammar = GrammarBuilder.grammar()
                .withAdded(rule(S, List.of(A)))
                .withAdded(rule(A, List.of(B)))
                .withAdded(rule(B, List.of(new EmptySymbol())))
                .withAdded(rule(B, List.of(a)))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(3, followSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(S)
        );

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(A)
        );

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(B)
        );
    }

    @Test
    public void testMutualRecursion() {
        var S = new StartSymbol();
        var A = nonTerminal("A");
        var B = nonTerminal("B");
        var a = terminal("a");
        var b = terminal("b");

        var grammar = GrammarBuilder.grammar()
                .withAdded(rule(S, List.of(A, B)))
                .withAdded(rule(A, List.of(B, a)))
                .withAdded(rule(A, List.of(new EmptySymbol())))
                .withAdded(rule(B, List.of(b)))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(3, followSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(S)
        );

        assertEquals(
                Set.of(b),
                followSet.getFor(A)
        );

        assertEquals(
                Set.of(a, new TerminalToken()),
                followSet.getFor(B)
        );
    }

    @Test
    public void testStartSymbolOnly() {
        var S = new StartSymbol();
        var a = terminal("a");

        var grammar = GrammarBuilder.grammar()
                .withAdded(rule(S, List.of(a)))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(1, followSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(S)
        );
    }

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

        var grammar = GrammarBuilder.grammar()
                .withAdded(rule(S, List.of(E)))
                .withAdded(rule(E, List.of(T, Ep)))
                .withAdded(rule(Ep, List.of(plus, T, Ep)))
                .withAdded(rule(Ep, List.of(new EmptySymbol())))
                .withAdded(rule(T, List.of(F, Tp)))
                .withAdded(rule(Tp, List.of(times, F, Tp)))
                .withAdded(rule(Tp, List.of(new EmptySymbol())))
                .withAdded(rule(F, List.of(open, E, close)))
                .withAdded(rule(F, List.of(id)))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(6, followSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(S)
        );

        assertEquals(
                Set.of(new TerminalToken(), close),
                followSet.getFor(E)
        );

        assertEquals(
                Set.of(new TerminalToken(), close),
                followSet.getFor(Ep)
        );

        assertEquals(
                Set.of(new TerminalToken(), plus, close),
                followSet.getFor(T)
        );

        assertEquals(
                Set.of(new TerminalToken(), plus, close),
                followSet.getFor(Tp)
        );

        assertEquals(
                Set.of(new TerminalToken(), plus, times, close),
                followSet.getFor(F)
        );
    }
}
