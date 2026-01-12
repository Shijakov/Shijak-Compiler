package com.company.compiler.parser.ll1;

import com.company.compiler.common.grammar.GrammarBuilder;
import com.company.compiler.common.symbol.EmptySymbol;
import com.company.compiler.common.symbol.StartSymbol;
import com.company.compiler.common.token.TerminalToken;
import com.company.compiler.parser.ll1.exceptions.GrammarIsLeftRecursive;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static com.company.compiler.helpers.Helpers.*;

public class FirstSetTest {
    @Test
    public void testSingleTerminalProduction() {
        var S = new StartSymbol();
        var A = nonTerminal("A");
        var a = terminal("a");

        var grammar = GrammarBuilder.grammar(A)
                .withAdded(rule(A, List.of(a)))
                .build();

        var firstSet = FirstSet.from(grammar);

        assertEquals(2, firstSet.size());

        assertEquals(
                Set.of(a),
                firstSet.getFor(S)
        );

        assertEquals(
                Set.of(a),
                firstSet.getFor(A)
        );

        assertEquals(
                Set.of(a),
                firstSet.getFor(rule(S, List.of(A, new TerminalToken())))
        );

        assertEquals(
                Set.of(a),
                firstSet.getFor(rule(A, List.of(a)))
        );
    }

    @Test
    public void testSingleEmptyProduction() {
        var S = new StartSymbol();
        var A = nonTerminal("A");
        var e = new EmptySymbol();

        var grammar = GrammarBuilder.grammar(A)
                .withAdded(rule(A, List.of(e)))
                .build();

        var firstSet = FirstSet.from(grammar);

        assertEquals(2, firstSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                firstSet.getFor(S)
        );

        assertEquals(
                Set.of(e),
                firstSet.getFor(A)
        );

        assertEquals(
                Set.of(new TerminalToken()),
                firstSet.getFor(rule(S, List.of(A, new TerminalToken())))
        );

        assertEquals(
                Set.of(e),
                firstSet.getFor(rule(A, List.of(e)))
        );
    }

    @Test
    public void testMultipleAlternatives() {
        var S = new StartSymbol();
        var A = nonTerminal("A");
        var a = terminal("a");
        var b = terminal("b");
        var c = terminal("c");

        var grammar = GrammarBuilder.grammar(A)
                .withAdded(rule(A, List.of(a)))
                .withAdded(rule(A, List.of(b)))
                .withAdded(rule(A, List.of(c)))
                .build();

        var firstSet = FirstSet.from(grammar);

        assertEquals(2, firstSet.size());

        assertEquals(
                Set.of(a, b, c),
                firstSet.getFor(S)
        );

        assertEquals(
                Set.of(a, b, c),
                firstSet.getFor(A)
        );

        assertEquals(
                Set.of(a, b, c),
                firstSet.getFor(rule(S, List.of(A, new TerminalToken())))
        );

        assertEquals(
                Set.of(a),
                firstSet.getFor(rule(A, List.of(a)))
        );

        assertEquals(
                Set.of(b),
                firstSet.getFor(rule(A, List.of(b)))
        );

        assertEquals(
                Set.of(c),
                firstSet.getFor(rule(A, List.of(c)))
        );
    }

    @Test
    public void testIndirectTerminalThroughNonTerminal() {
        var S = new StartSymbol();
        var A = nonTerminal("A");
        var B = nonTerminal("B");
        var a = terminal("a");

        var grammar = GrammarBuilder.grammar(A)
                .withAdded(rule(A, List.of(B)))
                .withAdded(rule(B, List.of(a)))
                .build();

        var firstSet = FirstSet.from(grammar);

        assertEquals(3, firstSet.size());

        assertEquals(
                Set.of(a),
                firstSet.getFor(S)
        );

        assertEquals(
                Set.of(a),
                firstSet.getFor(A)
        );

        assertEquals(
                Set.of(a),
                firstSet.getFor(B)
        );

        assertEquals(
                Set.of(a),
                firstSet.getFor(rule(S, List.of(A, new TerminalToken())))
        );

        assertEquals(
                Set.of(a),
                firstSet.getFor(rule(A, List.of(B)))
        );

        assertEquals(
                Set.of(a),
                firstSet.getFor(rule(B, List.of(a)))
        );
    }

    @Test
    public void testEmptyFollowedByTerminal() {
        var S = new StartSymbol();
        var A = nonTerminal("A");
        var B = nonTerminal("B");
        var C = nonTerminal("C");
        var a = terminal("a");

        var grammar = GrammarBuilder.grammar(A)
                .withAdded(rule(A, List.of(B, C)))
                .withAdded(rule(B, List.of(new EmptySymbol())))
                .withAdded(rule(C, List.of(a)))
                .build();

        var firstSet = FirstSet.from(grammar);

        assertEquals(4, firstSet.size());

        assertEquals(
                Set.of(a),
                firstSet.getFor(S)
        );

        assertEquals(
                Set.of(a),
                firstSet.getFor(A)
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(B)
        );

        assertEquals(
                Set.of(a),
                firstSet.getFor(C)
        );

        assertEquals(
                Set.of(a),
                firstSet.getFor(rule(S, List.of(A, new TerminalToken())))
        );

        assertEquals(
                Set.of(a),
                firstSet.getFor(rule(A, List.of(B, C)))
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(rule(B, List.of(new EmptySymbol())))
        );

        assertEquals(
                Set.of(a),
                firstSet.getFor(rule(C, List.of(a)))
        );
    }

    @Test
    public void testEmptyChain() {
        var S = new StartSymbol();
        var A = nonTerminal("A");
        var B = nonTerminal("B");
        var C = nonTerminal("C");

        var grammar = GrammarBuilder.grammar(A)
                .withAdded(rule(A, List.of(B, C)))
                .withAdded(rule(B, List.of(new EmptySymbol())))
                .withAdded(rule(C, List.of(new EmptySymbol())))
                .build();

        var firstSet = FirstSet.from(grammar);

        assertEquals(4, firstSet.size());

         assertEquals(
                Set.of(new TerminalToken()),
                firstSet.getFor(S)
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(A)
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(B)
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(C)
        );

        assertEquals(
                Set.of(new TerminalToken()),
                firstSet.getFor(rule(S, List.of(A, new TerminalToken())))
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(rule(A, List.of(B, C)))
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(rule(B, List.of(new EmptySymbol())))
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(rule(C, List.of(new EmptySymbol())))
        );
    }

    @Test
    public void testEmptyPlusTerminal() {
        var S = new StartSymbol();
        var A = nonTerminal("A");
        var B = nonTerminal("B");
        var c = terminal("c");

        var grammar = GrammarBuilder.grammar(A)
                .withAdded(rule(A, List.of(B)))
                .withAdded(rule(A, List.of(c)))
                .withAdded(rule(B, List.of(new EmptySymbol())))
                .build();

        var firstSet = FirstSet.from(grammar);

        assertEquals(3, firstSet.size());

        assertEquals(
                Set.of(new TerminalToken(), c),
                firstSet.getFor(S)
        );

        assertEquals(
                Set.of(new EmptySymbol(), c),
                firstSet.getFor(A)
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(B)
        );

        assertEquals(
                Set.of(new TerminalToken(), c),
                firstSet.getFor(rule(S, List.of(A, new TerminalToken())))
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(rule(A, List.of(B)))
        );

        assertEquals(
                Set.of(c),
                firstSet.getFor(rule(A, List.of(c)))
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(rule(B, List.of(new EmptySymbol())))
        );
    }

    @Test
    public void testLongerRightHandSide() {
        var S = new StartSymbol();
        var A = nonTerminal("A");
        var B = nonTerminal("B");
        var C = nonTerminal("C");
        var D = nonTerminal("D");
        var d = terminal("d");

        var grammar = GrammarBuilder.grammar(A)
                .withAdded(rule(A, List.of(B, C, D)))
                .withAdded(rule(B, List.of(new EmptySymbol())))
                .withAdded(rule(C, List.of(new EmptySymbol())))
                .withAdded(rule(D, List.of(d)))
                .build();

        var firstSet = FirstSet.from(grammar);

        assertEquals(5, firstSet.size());

        assertEquals(
                Set.of(d),
                firstSet.getFor(S)
        );

        assertEquals(
                Set.of(d),
                firstSet.getFor(A)
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(B)
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(C)
        );

        assertEquals(
                Set.of(d),
                firstSet.getFor(D)
        );

        assertEquals(
                Set.of(d),
                firstSet.getFor(rule(S, List.of(A, new TerminalToken())))
        );

        assertEquals(
                Set.of(d),
                firstSet.getFor(rule(A, List.of(B, C, D)))
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(rule(B, List.of(new EmptySymbol())))
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(rule(C, List.of(new EmptySymbol())))
        );

        assertEquals(
                Set.of(d),
                firstSet.getFor(rule(D, List.of(d)))
        );
    }

    @Test
    public void testMixedNullableNonNullable() {
        var S = new StartSymbol();
        var A = nonTerminal("A");
        var B = nonTerminal("B");
        var C = nonTerminal("C");
        var b = terminal("b");
        var c = terminal("c");

        var grammar = GrammarBuilder.grammar(A)
                .withAdded(rule(A, List.of(B, C)))
                .withAdded(rule(B, List.of(b)))
                .withAdded(rule(B, List.of(new EmptySymbol())))
                .withAdded(rule(C, List.of(c)))
                .build();

        var firstSet = FirstSet.from(grammar);

        assertEquals(4, firstSet.size());

        assertEquals(
                Set.of(b, c),
                firstSet.getFor(S)
        );

        assertEquals(
                Set.of(b, c),
                firstSet.getFor(A)
        );

        assertEquals(
                Set.of(b, new EmptySymbol()),
                firstSet.getFor(B)
        );

        assertEquals(
                Set.of(c),
                firstSet.getFor(C)
        );

        assertEquals(
                Set.of(b, c),
                firstSet.getFor(rule(S, List.of(A, new TerminalToken())))
        );

        assertEquals(
                Set.of(b, c),
                firstSet.getFor(rule(A, List.of(B, C)))
        );

        assertEquals(
                Set.of(b),
                firstSet.getFor(rule(B, List.of(b)))
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(rule(B, List.of(new EmptySymbol())))
        );

        assertEquals(
                Set.of(c),
                firstSet.getFor(rule(C, List.of(c)))
        );
    }

    @Test
    public void testRecursiveGrammar() {
        var A = nonTerminal("A");
        var B = nonTerminal("B");
        var a = terminal("a");

        var grammar = GrammarBuilder.grammar(A)
                .withAdded(rule(A, List.of(B)))
                .withAdded(rule(B, List.of(A)))
                .withAdded(rule(B, List.of(a)))
                .build();

        assertThrows(GrammarIsLeftRecursive.class, () -> FirstSet.from(grammar));
    }

    @Test
    public void testLeftRecursiveGrammar() {
        var A = nonTerminal("A");
        var a = terminal("a");
        var b = terminal("b");

        var grammar = GrammarBuilder.grammar(A)
                .withAdded(rule(A, List.of(A, a)))
                .withAdded(rule(A, List.of(b)))
                .build();

        assertThrows(GrammarIsLeftRecursive.class, () -> FirstSet.from(grammar));
    }

    @Test
    public void testMutualRecursionWithEmpty() {
        var S = new StartSymbol();
        var A = nonTerminal("A");
        var B = nonTerminal("B");
        var C = nonTerminal("C");
        var a = terminal("a");

        var grammar = GrammarBuilder.grammar(A)
                .withAdded(rule(A, List.of(B)))
                .withAdded(rule(B, List.of(C)))
                .withAdded(rule(C, List.of(new EmptySymbol())))
                .withAdded(rule(C, List.of(a)))
                .build();

        var firstSet = FirstSet.from(grammar);

        assertEquals(4, firstSet.size());

        assertEquals(
                Set.of(new TerminalToken(), a),
                firstSet.getFor(S)
        );

        assertEquals(
                Set.of(new EmptySymbol(), a),
                firstSet.getFor(A)
        );

        assertEquals(
                Set.of(new EmptySymbol(), a),
                firstSet.getFor(B)
        );

        assertEquals(
                Set.of(new EmptySymbol(), a),
                firstSet.getFor(C)
        );

        assertEquals(
                Set.of(new TerminalToken(), a),
                firstSet.getFor(rule(S, List.of(A, new TerminalToken())))
        );

        assertEquals(
                Set.of(new EmptySymbol(), a),
                firstSet.getFor(rule(A, List.of(B)))
        );

        assertEquals(
                Set.of(new EmptySymbol(), a),
                firstSet.getFor(rule(B, List.of(C)))
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(rule(C, List.of(new EmptySymbol())))
        );

        assertEquals(
                Set.of(a),
                firstSet.getFor(rule(C, List.of(a)))
        );
    }

    @Test
    public void testRealisticGrammar() {
        var S = new StartSymbol();
        var E = nonTerminal("A");
        var Ep = nonTerminal("Ep");
        var T = nonTerminal("T");
        var Tp = nonTerminal("Tp");
        var F = nonTerminal("F");
        var plus = terminal("+");
        var times = terminal("*");
        var open = terminal("(");
        var closed = terminal(")");
        var id = terminal("id");

        var grammar = GrammarBuilder.grammar(E)
                .withAdded(rule(E, List.of(T, Ep)))
                .withAdded(rule(Ep, List.of(plus, T, Ep)))
                .withAdded(rule(Ep, List.of(new EmptySymbol())))
                .withAdded(rule(T, List.of(F, Tp)))
                .withAdded(rule(Tp, List.of(times, F, Tp)))
                .withAdded(rule(Tp, List.of(new EmptySymbol())))
                .withAdded(rule(F, List.of(open, E, closed)))
                .withAdded(rule(F, List.of(id)))
                .build();

        var firstSet = FirstSet.from(grammar);

        assertEquals(6, firstSet.size());

        assertEquals(
                Set.of(open, id),
                firstSet.getFor(S)
        );

        assertEquals(
                Set.of(open, id),
                firstSet.getFor(E)
        );

        assertEquals(
                Set.of(plus, new EmptySymbol()),
                firstSet.getFor(Ep)
        );

        assertEquals(
                Set.of(open, id),
                firstSet.getFor(T)
        );

        assertEquals(
                Set.of(times, new EmptySymbol()),
                firstSet.getFor(Tp)
        );

        assertEquals(
                Set.of(open, id),
                firstSet.getFor(F)
        );

        assertEquals(
                Set.of(open, id),
                firstSet.getFor(rule(S, List.of(E, new TerminalToken())))
        );

        assertEquals(
                Set.of(open, id),
                firstSet.getFor(rule(E, List.of(T, Ep)))
        );

        assertEquals(
                Set.of(plus),
                firstSet.getFor(rule(Ep, List.of(plus, T, Ep)))
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(rule(Ep, List.of(new EmptySymbol())))
        );

        assertEquals(
                Set.of(open, id),
                firstSet.getFor(rule(T, List.of(F, Tp)))
        );

        assertEquals(
                Set.of(times),
                firstSet.getFor(rule(Tp, List.of(times, F, Tp)))
        );

        assertEquals(
                Set.of(new EmptySymbol()),
                firstSet.getFor(rule(Tp, List.of(new EmptySymbol())))
        );

        assertEquals(
                Set.of(open),
                firstSet.getFor(rule(F, List.of(open, E, closed)))
        );

        assertEquals(
                Set.of(id),
                firstSet.getFor(rule(F, List.of(id)))
        );
    }
}
