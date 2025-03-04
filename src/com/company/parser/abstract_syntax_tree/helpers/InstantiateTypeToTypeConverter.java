//package com.company.parser.abstract_syntax_tree.helpers;
//
//import com.company.parser.abstract_syntax_tree.ASTNodes;
//import com.company.symbol_table.variable_types.VarType;
//
//import java.util.function.Function;
//
//public class InstantiateTypeToTypeConverter implements Function<ASTNodes.ASTNode, VarType> {
//    @Override
//    public VarType apply(ASTNodes.ASTNode node) {
//        if (node instanceof ASTNodes.BoolInitType) {
//            return new VarType(VarType.PrimitiveType.BOOL,
//                    ((ASTNodes.BoolInitType) node).arrExt != null);
//        } else if (node instanceof ASTNodes.CharInitType) {
//            return new VarType(VarType.PrimitiveType.CHAR,
//                    ((ASTNodes.CharInitType) node).arrExt != null);
//        } else if (node instanceof ASTNodes.IntInitType) {
//            return new VarType(VarType.PrimitiveType.INT,
//                    ((ASTNodes.IntInitType) node).arrExt != null);
//        } else {
//            return new VarType(VarType.PrimitiveType.FLOAT,
//                    ((ASTNodes.FloatInitType) node).arrExt != null);
//        }
//    }
//}
