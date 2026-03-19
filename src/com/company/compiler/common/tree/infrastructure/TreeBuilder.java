package com.company.compiler.common.tree.infrastructure;

import com.company.compiler.common.tree.model.Node;
import com.company.compiler.common.tree.model.Tree;
import com.company.compiler.common.tree.exceptions.NoNextNodeInTreeBuilderException;

import java.util.*;

public class TreeBuilder {
    Tree tree;
    Queue<Node> nodeQueue;

    private TreeBuilder(Tree tree) {
        this.tree = tree;
        this.nodeQueue = new LinkedList<>();
        this.nodeQueue.add(tree.getRoot());
    }

    public static TreeBuilder from(Tree tree) {
        return new TreeBuilder(tree);
    }

    public TreeBuilder attachNodes(List<Node> nodes) throws NoNextNodeInTreeBuilderException {
        Node node = this.nodeQueue.poll();

        if (node == null)
            throw new NoNextNodeInTreeBuilderException();

        node.attachChildren(nodes);
        this.nodeQueue.addAll(nodes);

        return this;
    }

    public Tree build() {
        return this.tree;
    }
}
