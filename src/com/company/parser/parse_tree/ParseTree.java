package com.company.parser.parse_tree;

import com.company.exceptions.UnexpectedTokenException;
import com.company.lexer.Token;
import com.company.lexer.Token.TokenType;

import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

public class ParseTree {
    public Node root;

    public abstract static class Node {
        public Node firstChild = null;
        public Node neighbor = null;
        public Node parent = null;
        public int line = -1;

        public boolean isTerminal = false;

        public void attachNodes (List<Node> nodeList) {
            for (int idx = 0 ; idx < nodeList.size() ; idx++) {
                Node node = nodeList.get(idx);
                if (idx == 0) {
                    this.firstChild = node;
                }
                if (idx < nodeList.size() - 1) {
                    node.neighbor = nodeList.get(idx + 1);
                }
                node.parent = this;
            }
        }

        @Override
        public String toString() {
            return Integer.toString(line);
        }
    }

    public static class NonTerminalNode extends Node {
        public NodeType nodeType;
        public NonTerminalNode(NodeType nodeType) {
            super();
            this.isTerminal = false;
            this.nodeType = nodeType;
        }

        @Override
        public String toString() {
            return "NonTerminalNode{" +
                    "nodeType=" + nodeType +
                    "} - " + super.toString();
        }
    }

    public static class TerminalNode extends Node {
        public TokenType tokenType;
        public String value;

        public TerminalNode(TokenType tokenType, String value) {
            super();
            this.isTerminal = true;
            this.tokenType = tokenType;
            this.value = value;
        }

        @Override
        public String toString() {
            return "TerminalNode{" +
                    "tokenType=" + tokenType +
                    ", value='" + value + '\'' +
                    "} - " + super.toString();
        }
    }

    public ParseTree(List<Token> tokens) throws UnexpectedTokenException{
        Stack<Token> tokenStack = getTokenStack(tokens);
        Stack<Node> nodeStack = new Stack<>();
        LL1Table ll1 = new LL1Table();

        this.root = new NonTerminalNode(NodeType.S);

        nodeStack.push(this.root);

        while (!tokenStack.empty() && !nodeStack.empty()) {
            Node nextNode = nodeStack.peek();
            Token nextToken = tokenStack.peek();
            nextNode.line = nextToken.line;

            if (!nextNode.isTerminal) {
                Consumer<Stack<Node>> consumer = ll1.table
                        .get(new LL1Table.Pair(((NonTerminalNode)nextNode).nodeType, nextToken.getTokenType()));

                if (consumer == null) {
                    throw new UnexpectedTokenException(nextToken, (NonTerminalNode)nextNode, nextToken.line);
                }

                consumer.accept(nodeStack);
            } else {
                TerminalNode terminalNode = (TerminalNode) nextNode;
                if (!terminalNode.tokenType.equals(nextToken.getTokenType())) {
                    throw new UnexpectedTokenException(nextToken, terminalNode, nextToken.line);
                }

                terminalNode.value = nextToken.getValue();
                nodeStack.pop();
                tokenStack.pop();
            }
        }
    }

    private Stack<Token> getTokenStack(List<Token> tokens) {
        Stack<Token> tokenStack = new Stack<>();
        tokenStack.push(new Token(TokenType.TERMINAL, "$", -1));

        for (int i = tokens.size() - 1; i >= 0 ; i--) {
            tokenStack.push(tokens.get(i));
        }

        return tokenStack;
    }

    private void printRecursive(Node node) {
        Node child = node.firstChild;
        Node anotherChild = node.firstChild;
        if (node.isTerminal) {
            System.out.println(((TerminalNode)node).value);
        }
        if (node.isTerminal && node.firstChild != null) {
            System.out.println("ERROR: terminal node has child");
        }
        System.out.printf("\nCurrent node is: %s\n", node);
        while(anotherChild != null) {
            System.out.printf("Child is: %s\n", anotherChild);
            anotherChild = anotherChild.neighbor;
        }
        while(child != null) {
            printRecursive(child);
            child = child.neighbor;
        }
    }

    public void print() {
        printRecursive(this.root);
    }

}
