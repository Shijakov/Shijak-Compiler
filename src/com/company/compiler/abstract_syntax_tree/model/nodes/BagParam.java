package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class BagParam extends ASNode {
    public String identifier;
    public ASNode type;
    public ASNode nextParam;

    @Override
    public String getName() {
        return identifier;
    }

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(type, nextParam);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 2);
        this.type = (ASNode) children.get(0);
        this.nextParam = (ASNode) children.get(1);
    }
}
