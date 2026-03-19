package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class UnaryOperator extends ASNode {
    public String operator;
    public ASNode left;

    @Override
    public String getName() {
        return operator;
    }

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(left);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 1);
        this.left = (ASNode) children.getFirst();
    }
}
