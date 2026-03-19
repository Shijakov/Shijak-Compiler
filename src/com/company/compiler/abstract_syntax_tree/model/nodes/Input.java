package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class Input extends ASNode {
    public ASNode assignableInstance;

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(assignableInstance);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 1);
        this.assignableInstance = (ASNode) children.getFirst();
    }
}
