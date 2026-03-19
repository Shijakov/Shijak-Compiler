package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class FunctionDefinition extends ASNode {
    public String functionName;
    public ASNode paramList;
    public ASNode returnType;
    public ASNode firstStatement;

    @Override
    public String getName() {
        return functionName;
    }

    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList(paramList, returnType, firstStatement);
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 3);
        this.paramList = (ASNode) children.get(0);
        this.returnType = (ASNode) children.get(1);
        this.firstStatement = (ASNode) children.get(2);
    }
}
