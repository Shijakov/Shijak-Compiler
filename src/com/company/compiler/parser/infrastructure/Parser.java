package com.company.compiler.parser.infrastructure;

import com.company.compiler.common.grammar.Grammar;
import com.company.compiler.common.symbol.Symbol;
import com.company.compiler.common.token.RecognisedToken;
import com.company.compiler.common.tree.Tree;

import java.util.List;

public interface Parser {
    Tree<Symbol> parse(List<RecognisedToken> tokens, Grammar grammar);
}
