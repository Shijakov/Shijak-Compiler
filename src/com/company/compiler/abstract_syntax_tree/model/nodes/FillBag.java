package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class FillBag extends ASNode {
    public String bagName;
    public ASNode firstFillBagArgument;
    public ASNode assignableInstance;

    @Override
    public String getName() {
        return bagName;
    }

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(firstFillBagArgument, assignableInstance);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 2);
        this.firstFillBagArgument = (ASNode) children.get(0);
        this.assignableInstance = (ASNode) children.get(1);
    }
}
