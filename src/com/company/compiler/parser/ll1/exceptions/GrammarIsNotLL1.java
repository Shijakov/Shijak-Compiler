package com.company.compiler.parser.ll1.exceptions;

import com.company.compiler.common.exceptions.LanguageDefinitionException;

public class GrammarIsNotLL1 extends LanguageDefinitionException {
    public GrammarIsNotLL1() {
        super("Grammar is not LL1");
    }
}
