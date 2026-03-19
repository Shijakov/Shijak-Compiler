package com.company.compiler.abstract_syntax_tree.model.nodes;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.tree.model.Node;

import java.util.Arrays;
import java.util.List;

public class Break extends ASNode {
    @Override
    public List<ASNode> getChildren() {
        return Arrays.asList();
    }

    @Override
    public void attachChildren(List<? extends Node> children) {
        assertLength(children, 0);
    }
}
