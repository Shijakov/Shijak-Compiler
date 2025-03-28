package com.company.exceptions;

import com.company.parser.abstract_syntax_tree.ASTNodes;

public abstract class LineInfoException extends Exception {
    public LineInfoException(String message, int line) {
        super(String.format("%s - Occured on line %d", message, line));
    }
}
