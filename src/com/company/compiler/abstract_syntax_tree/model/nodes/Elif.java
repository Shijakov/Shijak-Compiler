package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class Elif extends ASNode {
    public ASNode condition;
    public ASNode firstStatement;
    public ASNode elif;

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(condition, firstStatement, elif);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 3);
        this.condition = (ASNode) children.get(0);
        this.firstStatement = (ASNode) children.get(1);
        this.elif = (ASNode) children.get(2);
    }
}
