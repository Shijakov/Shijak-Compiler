package com.company.compiler.parser.model;

import com.company.compiler.common.tree.model.Tree;

import java.util.Stack;

public class ParseTree implements Tree {
    protected ParseNode root;

    public ParseTree(ParseNode root) {
        this.root = root;
    }

    public ParseNode getRoot() {
        return this.root;
    }

    public void print() {
        record Entry (ParseNode node, int level) {}

        var stack = new Stack<Entry>();
        stack.push(new Entry(root, 0));

        while (!stack.empty()) {
            ParseNode next = stack.peek().node;
            int level = stack.pop().level;
            System.out.print("=".repeat(level));
            System.out.println(next);
            var children = next.getChildren();
            for (var child : children)
                stack.add(new Entry(child, level + 1));
        }
    }
}
