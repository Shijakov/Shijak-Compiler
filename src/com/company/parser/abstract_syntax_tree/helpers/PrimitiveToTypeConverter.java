package com.company.parser.abstract_syntax_tree.helpers;

import com.company.parser.abstract_syntax_tree.ASTNodes;
import com.company.symbol_table.VarType;

import java.util.function.Function;

public class PrimitiveToTypeConverter implements Function<ASTNodes.ASTNode, VarType> {
    @Override
    public VarType apply(ASTNodes.ASTNode node) {
        if (node instanceof ASTNodes.BoolConstant) {
            VarType tmp = new VarType(VarType.PrimitiveType.BOOL, false);
            tmp.value = ((ASTNodes.BoolConstant) node).value;
            return tmp;
        } else if (node instanceof ASTNodes.CharConstant) {
            VarType tmp = new VarType(VarType.PrimitiveType.CHAR, false);
            tmp.value = ((ASTNodes.CharConstant) node).value;
            return tmp;
        } else if (node instanceof ASTNodes.IntConstant) {
            VarType tmp = new VarType(VarType.PrimitiveType.INT, false);
            tmp.value = ((ASTNodes.IntConstant) node).value;
            return tmp;
        } else {
            VarType tmp = new VarType(VarType.PrimitiveType.FLOAT, false);
            tmp.value = ((ASTNodes.FloatConstant) node).value;
            return tmp;
        }
    }
}
