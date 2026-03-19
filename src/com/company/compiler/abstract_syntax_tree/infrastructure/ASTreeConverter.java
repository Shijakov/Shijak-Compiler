package com.company.compiler.abstract_syntax_tree.infrastructure;

import com.company.compiler.abstract_syntax_tree.model.ASTree;
import com.company.compiler.parser.model.ParseNode;
import com.company.compiler.parser.model.ParseTree;

import java.util.HashMap;
import java.util.Map;

public class ASTreeConverter {
    private static ASTreeConverter instance;

    private final Map<Class<? extends ParseNode>, ASNodeConverter> converterMap;

    private ASTreeConverter() {
        this.converterMap = new HashMap<>();
    }

    public static ASTreeConverter getInstance() {
        if (instance == null) {
            instance = new ASTreeConverter();
        }
        return instance;
    }

    public void registerConverter(Class<? extends ParseNode> parseNode, ASNodeConverter converter) {
        this.converterMap.put(parseNode, converter);
    }

    public ASNodeConverter getConverter(ParseNode node) {
        return this.converterMap.get(node.getClass());
    }

    public ASTree convert(ParseTree parseTree) {
        var asRoot = getConverter(parseTree.getRoot()).convert(parseTree.getRoot());

        return new ASTree(asRoot);
    }
}
