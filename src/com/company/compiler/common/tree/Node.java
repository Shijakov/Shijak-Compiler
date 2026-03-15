package com.company.compiler.common.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node <T> {
    protected Node<T> firstChild;
    protected Node<T> parent;
    protected Node<T> neighbor;

    protected T value;

    public Node(T value) {
        this.value = value;
    }

    public void attachChildren(List<Node<T>> children) {
        if (children.isEmpty()) return;

        Node<T> prev = null;
        this.firstChild = children.getFirst();

        for (Node<T> child : children) {
            child.parent = this;
            if (prev != null)
                prev.neighbor = child;

            prev = child;
        }
    }

    public List<Node<T>> getChildren() {
        var result = new ArrayList<Node<T>>();
        var curr = this.firstChild;

        while (curr != null) {
            result.add(curr);
            curr = curr.neighbor;
        }

        return result;
    }

    public T getValue() {
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
        Node<?> node = (Node<?>) object;
        return Objects.equals(value, node.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
