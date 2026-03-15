package com.company.compiler.parser.infrastructure;

import com.company.compiler.common.grammar.GrammarBuilder;
import com.company.compiler.common.symbol.EmptySymbol;
import com.company.compiler.common.symbol.StartSymbol;
import com.company.compiler.common.symbol.Symbol;
import com.company.compiler.common.token.RecognisedToken;
import com.company.compiler.common.token.TerminalToken;
import com.company.compiler.common.tree.Node;
import com.company.compiler.common.tree.exceptions.NoNextNodeInTreeBuilderException;
import com.company.compiler.common.tree.infrastructure.TreeBuilder;
import com.company.compiler.common.tree.infrastructure.TreeComparator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static com.company.compiler.helpers.Helpers.*;

public class LL1ParserTest {
    @Test
    public void test() throws NoNextNodeInTreeBuilderException {
        var S = new StartSymbol();
        var E = nonTerminal("E");
        var Ep = nonTerminal("Ep");
        var T = nonTerminal("T");
        var Tp = nonTerminal("Tp");
        var F = nonTerminal("F");
        var plus = token("\\+");
        var times = token("\\*");
        var open = token("\\(");
        var close = token("\\)");
        var id = token("id");
        var tt = new TerminalToken();
        var emp = new EmptySymbol();
        RecognisedToken.match(id, "id", 1);

        var recognizedTokens = List.of(
                RecognisedToken.match(id, "id", 1),
                RecognisedToken.match(plus, "+", 1),
                RecognisedToken.match(id, "id", 1),
                RecognisedToken.match(tt, "$", 1)
        );

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

        var parser = new LL1Parser();
        var tree = parser.parse(recognizedTokens, grammar);

        var expected = TreeBuilder.from(new Node<Symbol>(S))
                .attachNodes(List.of(node(E), node(tt)))
                .attachNodes(List.of(node(T), node(Ep)))
                .attachNodes(List.of())
                .attachNodes(List.of(node(F), node(Tp)))
                .attachNodes(List.of(node(plus), node(T), node(Ep)))
                .attachNodes(List.of(node(id)))
                .attachNodes(List.of(node(emp)))
                .attachNodes(List.of())
                .attachNodes(List.of(node(F), node(Tp)))
                .attachNodes(List.of(node(emp)))
                .attachNodes(List.of())
                .attachNodes(List.of())
                .attachNodes(List.of(node(id)))
                .attachNodes(List.of(node(emp)))
                .build();

        assertTrue(TreeComparator.areEqual(expected, tree));
    }
}
