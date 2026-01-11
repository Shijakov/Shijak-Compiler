package com.company.compiler.common.grammar;

import java.util.ArrayList;
import java.util.List;

public class GrammarBuilder {
    List<Rule> rules;

    private GrammarBuilder() {
        this.rules = new ArrayList<>();
    }

    public static GrammarBuilder grammar() {
        return new GrammarBuilder();
    }

    public GrammarBuilder withAdded(Rule rule) {
        this.rules.add(rule);
        return this;
    }

    public Grammar build() {
        return Grammar.from(rules);
    }
}
