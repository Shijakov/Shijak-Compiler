package com.company.compiler.common.tree.infrastructure;

import com.company.compiler.common.tree.model.Node;
import com.company.compiler.common.tree.model.Tree;

public class TreeComparator {

    public static <T> boolean areEqual(Tree t1, Tree t2) {
        return areEqual(t1.getRoot(), t2.getRoot());
    }

    private static <T> boolean areEqual(Node n1, Node n2) {
        if (n1 == null && n2 == null)
            return true;
        if (n1 == null || n2 == null)
            return false;
        if (!n1.equals(n2))
            return false;

        var childrenN1 = n1.getChildren();
        var childrenN2 = n2.getChildren();

        if (childrenN1.size() != childrenN2.size())
            return false;

        var rez = true;
        for (int i = 0 ; i < childrenN1.size() ; i++) {
            rez = rez && areEqual(childrenN1.get(i), childrenN2.get(i));
        }

        return rez;
    }
}
