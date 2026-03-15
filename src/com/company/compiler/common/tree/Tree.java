package com.company.compiler.common.tree;

public class Tree <T> {
    protected Node<T> root;

    public Tree(Node<T> root) {
        this.root = root;
    }

    public Node<T> getRoot() {
        return this.root;
    }
}
