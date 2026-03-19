package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class Definition extends ASNode {
    public ASNode firstDefinition;

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(firstDefinition);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 3);
        this.firstDefinition = (ASNode) children.getFirst();
    }
}
