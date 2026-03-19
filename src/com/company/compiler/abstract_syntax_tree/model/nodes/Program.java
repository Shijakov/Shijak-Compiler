package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class Program extends ASNode {
    public ASNode definition;
    public ASNode constructList;

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(definition, constructList);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 2);
        this.definition = (ASNode) children.get(0);
        this.constructList = (ASNode) children.get(1);
    }
}
