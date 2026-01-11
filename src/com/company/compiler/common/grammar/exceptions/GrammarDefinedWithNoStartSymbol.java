package com.company.compiler.common.grammar.exceptions;

import com.company.compiler.common.exceptions.LanguageDefinitionException;

public class GrammarDefinedWithNoStartSymbol extends LanguageDefinitionException {
    public GrammarDefinedWithNoStartSymbol() {
        super("Grammar defined with no start symbol exception");
    }
}
