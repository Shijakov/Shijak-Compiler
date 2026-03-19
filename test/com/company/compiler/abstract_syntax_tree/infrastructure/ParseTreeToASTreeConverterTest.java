package com.company.compiler.abstract_syntax_tree.infrastructure;

import com.company.compiler.common.grammar.GrammarBuilder;
import com.company.compiler.common.symbol.EmptySymbol;
import com.company.compiler.common.symbol.StartSymbol;
import com.company.compiler.common.token.RecognisedToken;
import com.company.compiler.common.token.TerminalToken;
import com.company.compiler.common.tree.exceptions.NoNextNodeInTreeBuilderException;
import com.company.compiler.common.tree.infrastructure.TreeBuilder;
import com.company.compiler.parser.model.ParseNode;
import com.company.compiler.parser.model.ParseTree;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.company.compiler.helpers.Helpers.*;
import static com.company.compiler.helpers.Helpers.node;

public class ParseTreeToASTreeConverterTest {
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

        var parseTree = TreeBuilder.from(new ParseTree(new ParseNode(S)))
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
    }
}
