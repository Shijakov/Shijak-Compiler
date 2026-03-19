package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class Statement extends ASNode {
    public ASNode statement;
    public ASNode nextStatement;

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(statement, nextStatement);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 2);
        this.statement = (ASNode) children.get(0);
        this.nextStatement = (ASNode) children.get(1);
    }
}
