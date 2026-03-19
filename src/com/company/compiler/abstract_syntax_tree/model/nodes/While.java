package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class While extends ASNode {
    public ASNode condition;
    public ASNode firstStatement;

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(condition, firstStatement);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 2);
        this.condition = (ASNode) children.get(0);
        this.firstStatement = (ASNode) children.get(1);
    }
}
