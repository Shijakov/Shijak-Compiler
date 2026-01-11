package com.company.old.exceptions.dev_exceptions;

import com.company.old.parser.abstract_syntax_tree.ASTNodes;

public class TypeToTypeConversionException extends Exception {
    public TypeToTypeConversionException(ASTNodes.ASTNode node) {
        super("Cannot convert " + node + " to type ");
    }
}
