package com.company.compiler.parser.model;

import com.company.compiler.common.tree.model.Tree;

public class ParseTree implements Tree {
    protected ParseNode root;

    public ParseTree(ParseNode root) {
        this.root = root;
    }

    public ParseNode getRoot() {
        return this.root;
    }
}
