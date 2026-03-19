package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class DefinitionInstance extends ASNode {
    public ASNode primitiveConstant;
    public String identifier;
    public ASNode nextDefinition;

    @Override
    public String getName() {
        return identifier;
    }

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(primitiveConstant, nextDefinition);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 3);
        this.primitiveConstant = (ASNode) children.get(0);
        this.nextDefinition = (ASNode) children.get(1);
    }
}
