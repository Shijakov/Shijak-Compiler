package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class BagDefinition extends ASNode {
    public String bagName;
    public ASNode paramList;

    @Override
    public String getName() {
        return bagName;
    }

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(paramList);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 1);
        this.paramList = (ASNode) children.getFirst();
    }
}
