package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class AllocArr extends ASNode {
    public ASNode type;
    public ASNode expression;
    public ASNode assignableInstance;

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(type, expression, assignableInstance);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 3);
        this.type = (ASNode) children.get(0);
        this.expression = (ASNode) children.get(1);
        this.assignableInstance = (ASNode) children.get(2);
    }
}
