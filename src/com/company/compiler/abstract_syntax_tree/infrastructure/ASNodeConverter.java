package com.company.compiler.abstract_syntax_tree.infrastructure;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.common.token.RecognisedToken;
import com.company.compiler.parser.model.ParseNode;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class ASNodeConverter {
    protected ASNodeConverter(Class<? extends ParseNode> parseNode) {
        ASTreeConverter.getInstance().registerConverter(parseNode, this);
    }

    protected void initialize(ASNode asNode, ParseNode parseNode) {
        asNode.line = parseNode.line;
    }

    protected String getValue(ParseNode node) {
        return ((RecognisedToken) node.getValue()).getValue();
    }

    protected ASNode convert(ParseNode parseNode) {
        return ASTreeConverter.getInstance().getConverter(parseNode).convert(parseNode);
    }

    protected abstract ASNode doConvert(ParseNode parseNode);
}
