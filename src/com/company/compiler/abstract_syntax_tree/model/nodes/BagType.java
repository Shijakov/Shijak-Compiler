package com.company.compiler.abstract_syntax_tree.model.nodes;

public class BagType extends Type {
    public String bagName;

    @Override
    public String getName() {
        return bagName;
    }
}
