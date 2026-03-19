package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class BinaryOperator extends ASNode {
    public String operator;
    public ASNode left;
    public ASNode right;

    @Override
    public String getName() {
        return operator;
    }

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(left, right);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 2);
        this.left = (ASNode) children.get(0);
        this.right = (ASNode) children.get(1);
    }
}
