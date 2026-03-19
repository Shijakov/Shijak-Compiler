package com.company.compiler.common.tree.model;

import java.util.List;

public interface Node {
    List<? extends Node> getChildren();

    void attachChildren(List<? extends Node> children);
}
