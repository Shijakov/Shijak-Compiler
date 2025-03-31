package com.company.exceptions.dev_exceptions;

import com.company.parser.abstract_syntax_tree.ASTNodes;

public class TypeToTypeConversionException extends Exception {
    public TypeToTypeConversionException(ASTNodes.ASTNode node) {
        super("Cannot convert " + node + " to type ");
    }
}
