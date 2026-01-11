package com.company.old.exceptions.dev_exceptions;

import com.company.old.parser.abstract_syntax_tree.ASTNodes;

public class PrimitiveTypeToTypeConversionException extends Exception {
    public PrimitiveTypeToTypeConversionException(ASTNodes.ASTNode node) {
        super("Cannot convert " + node + " to primitive type ");
    }
}
