package com.company.compiler.common.grammar;

import com.company.compiler.common.exceptions.DevException;
import com.company.compiler.common.symbol.NonTerminal;
import com.company.compiler.common.symbol.StartSymbol;
import com.company.compiler.common.token.TerminalToken;

import java.util.ArrayList;
import java.util.List;

public class GrammarBuilder {
    NonTerminal start;
    List<Rule> rules;

    private GrammarBuilder(NonTerminal start) {
        this.start = start;
        this.rules = new ArrayList<>();
    }

    public static GrammarBuilder grammar(NonTerminal start) {
        return new GrammarBuilder(start);
    }

    public GrammarBuilder withAdded(Rule rule) {
        this.rules.add(rule);
        return this;
    }

    public Grammar build() {
        if (start.equals(new StartSymbol())) {
            throw new DevException("Don't define a non terminal with name S");
        }

        this.rules.add(new Rule(new StartSymbol(), List.of(start, new TerminalToken())));
        return Grammar.from(rules);
    }
}
