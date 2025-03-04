package com.company.parser.abstract_syntax_tree.helpers;

import com.company.parser.abstract_syntax_tree.ASTNodes;
import com.company.symbol_table.variable_types.*;

import java.util.function.Function;

public class PrimitiveToTypeConverter implements Function<ASTNodes.Constant, VarType> {
    @Override
    public VarType apply(ASTNodes.Constant node) {
        return switch (node) {
            case ASTNodes.BoolConstant _ -> new BoolType(false);
            case ASTNodes.CharConstant _ -> new CharType(false);
            case ASTNodes.IntConstant _ -> new IntType(false);
            case ASTNodes.FloatConstant _ -> new FloatType(false);
            default -> null;
        };
    }
}
