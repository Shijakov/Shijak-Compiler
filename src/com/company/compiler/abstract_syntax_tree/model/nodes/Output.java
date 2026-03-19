package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class Output extends ASNode {
    public ASNode expression;

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(expression);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 1);
        this.expression = (ASNode) children.getFirst();
    }
}
