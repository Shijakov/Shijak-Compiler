package com.company.compiler.parser.model;

import com.company.compiler.common.symbol.Symbol;
import com.company.compiler.common.tree.model.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParseNode implements Node {
    public ParseNode firstChild;
    public ParseNode parent;
    public ParseNode neighbor;

    protected Symbol value;

    public int line;

    public ParseNode(Symbol value) {
        this.value = value;
    }

    public void attachChildren(List<? extends Node> children) {
        if (children.isEmpty()) return;

        ParseNode prev = null;
        this.firstChild = (ParseNode) children.getFirst();

        for (Node node : children) {
            ParseNode child = (ParseNode) node;

            child.parent = this;
            if (prev != null)
                prev.neighbor = child;

            prev = child;
        }
    }

    public List<ParseNode> getChildren() {
        var result = new ArrayList<ParseNode>();
        var curr = this.firstChild;

        while (curr != null) {
            result.add(curr);
            curr = curr.neighbor;
        }

        return result;
    }

    public Symbol getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Node(" +
                value +
                ')';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        ParseNode node = (ParseNode) object;
        return Objects.equals(value, node.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
