package com.company.compiler.abstract_syntax_tree.model;

import com.company.compiler.abstract_syntax_tree.exceptions.IllegalAttachmentOfNodesException;
import com.company.compiler.common.exceptions.DevException;
import com.company.compiler.common.tree.model.Node;

import java.util.List;

public abstract class ASNode implements Node {
    public int line;

    public String getNodeType() {
        return this.getClass().getSimpleName();
    }

    public String getName() {
        return null;
    }

    protected void assertLength(List<?> nodes, int len) {
        if (nodes.size() != len) {
            throw new DevException("Illegal attachments of nodes");
        }
    }

    public void print(int n) {
        System.out.printf("%d ", n);
        for (int i = 0; i < n; i++) {
            System.out.print("-");
        }
        String name = this.getName();
        if (name != null) {
            System.out.printf("%s (%s)\n", this.getNodeType(), name);
        } else {
            System.out.println(this.getNodeType());
        }

        for (Node node : this.getChildren()) {
            ASNode child = (ASNode) node;
            if (child != null) {
                child.print(n + 1);
            }
        }
    }
}
