package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class BagCallExtension extends ASNode {
    public String fieldName;
    public ASNode callExtension;

    @Override
    public String getName() {
        return fieldName;
    }

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(callExtension);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 1);
        this.callExtension = (ASNode) children.getFirst();
    }
}
