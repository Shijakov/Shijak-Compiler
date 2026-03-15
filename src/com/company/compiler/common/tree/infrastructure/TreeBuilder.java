package com.company.compiler.common.tree.infrastructure;

import com.company.compiler.common.tree.Node;
import com.company.compiler.common.tree.Tree;
import com.company.compiler.common.tree.exceptions.NoNextNodeInTreeBuilderException;

import java.util.*;

public class TreeBuilder <T> {
    Tree<T> tree;
    Queue<Node<T>> nodeQueue;

    private TreeBuilder(Node<T> root) {
        this.tree = new Tree<>(root);
        this.nodeQueue = new LinkedList<>();
        this.nodeQueue.add(this.tree.getRoot());
    }

    public static <T> TreeBuilder<T> from (Node<T> root) {
        return new TreeBuilder<>(root);
    }

    public TreeBuilder<T> attachNodes(List<Node<T>> nodes) throws NoNextNodeInTreeBuilderException {
        Node<T> node = this.nodeQueue.poll();

        if (node == null)
            throw new NoNextNodeInTreeBuilderException();

        node.attachChildren(nodes);
        this.nodeQueue.addAll(nodes);

        return this;
    }

    public Tree<T> build() {
        return this.tree;
    }
}
