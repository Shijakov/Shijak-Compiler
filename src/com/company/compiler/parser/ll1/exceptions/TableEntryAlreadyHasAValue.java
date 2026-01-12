package com.company.compiler.parser.ll1.exceptions;

import com.company.compiler.common.exceptions.LanguageDefinitionException;

public class TableEntryAlreadyHasAValue extends LanguageDefinitionException {
    public TableEntryAlreadyHasAValue() {
        super("Table entry already has a value");
    }
}
