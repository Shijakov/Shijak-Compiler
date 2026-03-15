package com.company.compiler.parser.infrastructure;

import com.company.compiler.common.grammar.Grammar;
import com.company.compiler.common.grammar.Rule;
import com.company.compiler.common.symbol.EmptySymbol;
import com.company.compiler.common.symbol.NonTerminal;
import com.company.compiler.common.symbol.StartSymbol;
import com.company.compiler.common.symbol.Symbol;
import com.company.compiler.common.token.RecognisedToken;
import com.company.compiler.common.tree.Node;
import com.company.compiler.common.tree.Tree;
import com.company.compiler.parser.exceptions.NoEntryInTableException;
import com.company.compiler.parser.exceptions.UnexpectedTokenException;
import com.company.compiler.parser.ll1.LL1Table;

import java.util.List;
import java.util.Stack;

public class LL1Parser implements Parser {

    @Override
    public Tree<Symbol> parse(List<RecognisedToken> tokens, Grammar grammar) {
        var table = LL1Table.from(grammar);

        var tree = new Tree<Symbol>(new Node<>(new StartSymbol()));

        var nodeStack = new Stack<Node<Symbol>>();
        var tokenStack = new Stack<RecognisedToken>();

        nodeStack.push(tree.getRoot());
        pushToStack(tokenStack, tokens);

        while (true) {
            var currNode = nodeStack.pop();
            var currToken = tokenStack.peek();

            if (currToken.equals(currNode.getValue())) {
                tokenStack.pop();
                if (tokenStack.empty() && nodeStack.empty()) {
                    break;
                }
                continue;
            }

            Rule tableEntry;

            try {
                tableEntry = table.getRule((NonTerminal) currNode.getValue(), currToken);
            } catch (NoEntryInTableException e) {
                throw new UnexpectedTokenException(currToken, currToken.getLine());
            }

            var symbols = tableEntry.getRight();

            var nodes = toNodes(symbols);

            currNode.attachChildren(nodes);

            if (symbols.size() == 1 && symbols.getFirst().equals(new EmptySymbol()))
                continue;

            pushToStack(nodeStack, nodes);
        }

        return tree;
    }

    private List<Node<Symbol>> toNodes(List<Symbol> symbols) {
        return symbols.stream().map(Node::new).toList();
    }

    private <T> void pushToStack(Stack<T> stack, List<? extends T> objects) {
        for (int i = objects.size() - 1 ; i >= 0 ; i--) {
            stack.push(objects.get(i));
        }
    }

}
