package com.company.parser.parse_tree;

import com.company.exceptions.UnexpectedTokenException;
import com.company.lexer.Token;

import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

public class SyntaxAnalyzer {

    public static ParseTree analyze(List<Token> tokens) throws UnexpectedTokenException {
        Stack<Token> tokenStack = getTokenStack(tokens);
        Stack<ParseTree.Node> nodeStack = new Stack<>();
        LL1Table ll1 = new LL1Table();
        ParseTree tree = new ParseTree();

        tree.root = new ParseTree.NonTerminalNode(NodeType.S);

        nodeStack.push(tree.root);

        while (!tokenStack.empty() && !nodeStack.empty()) {
            ParseTree.Node nextNode = nodeStack.peek();
            Token nextToken = tokenStack.peek();
            nextNode.line = nextToken.line;

            if (!nextNode.isTerminal) {
                Consumer<Stack<ParseTree.Node>> consumer = ll1.table
                        .get(new LL1Table.Pair(((ParseTree.NonTerminalNode)nextNode).nodeType, nextToken.getTokenType()));

                if (consumer == null) {
                    throw new UnexpectedTokenException(nextToken, (ParseTree.NonTerminalNode)nextNode, nextToken.line);
                }

                consumer.accept(nodeStack);
            } else {
                ParseTree.TerminalNode terminalNode = (ParseTree.TerminalNode) nextNode;
                if (!terminalNode.tokenType.equals(nextToken.getTokenType())) {
                    throw new UnexpectedTokenException(nextToken, terminalNode, nextToken.line);
                }

                terminalNode.value = nextToken.getValue();
                nodeStack.pop();
                tokenStack.pop();
            }
        }

        return tree;
    }

    private static Stack<Token> getTokenStack(List<Token> tokens) {
        Stack<Token> tokenStack = new Stack<>();
        tokenStack.push(new Token(Token.TokenType.TERMINAL, "$", -1));

        for (int i = tokens.size() - 1; i >= 0 ; i--) {
            tokenStack.push(tokens.get(i));
        }

        return tokenStack;
    }
}
