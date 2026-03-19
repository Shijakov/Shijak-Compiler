package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class ExpressionList extends ASNode {
    public ASNode exprOrCloser;
    public ASNode nextExprList;

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(exprOrCloser, nextExprList);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 3);
        this.exprOrCloser = (ASNode) children.get(0);
        this.nextExprList = (ASNode) children.get(1);
    }
}
