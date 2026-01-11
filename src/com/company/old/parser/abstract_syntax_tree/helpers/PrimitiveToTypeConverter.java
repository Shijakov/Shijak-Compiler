package com.company.old.parser.abstract_syntax_tree.helpers;

import com.company.old.parser.abstract_syntax_tree.ASTNodes;
import com.company.old.symbol_table.variable_types.*;

import java.util.function.Function;

public class PrimitiveToTypeConverter implements Function<ASTNodes.Constant, VarType> {
    @Override
    public VarType apply(ASTNodes.Constant node) {
        return switch (node) {
            case ASTNodes.BoolConstant boolConstant_ -> new BoolType(false);
            case ASTNodes.CharConstant charConstant -> new CharType(false);
            case ASTNodes.IntConstant intConstant -> new IntType(false);
            case ASTNodes.FloatConstant floatConstant -> new FloatType(false);
            default -> null;
        };
    }
}
