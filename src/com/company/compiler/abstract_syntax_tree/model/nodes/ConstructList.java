package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class ConstructList extends ASNode {
    public ASNode construct;
    public ASNode nextConstruct;

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(construct, nextConstruct);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 2);
        this.construct = (ASNode) children.get(0);
        this.nextConstruct = (ASNode) children.get(1);
    }
}
