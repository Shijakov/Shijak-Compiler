package com.company.compiler.parser.ll1.exceptions;

import com.company.compiler.common.exceptions.LanguageDefinitionException;

public class GrammarIsLeftRecursive extends LanguageDefinitionException {
    public GrammarIsLeftRecursive() {
        super("Grammar is left recursive");
    }
}
