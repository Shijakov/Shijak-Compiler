//package com.company.parser.abstract_syntax_tree.helpers;
//
//import com.company.parser.abstract_syntax_tree.ASTNodes;
//import com.company.symbol_table.VarType;
//
//import java.util.function.Function;
//
//public class TypeToTypeConverter implements Function<ASTNodes.ASTNode, VarType> {
//    @Override
//    public VarType apply(ASTNodes.ASTNode node) {
//        if (node instanceof ASTNodes.BoolType) {
//            return new VarType(VarType.PrimitiveType.BOOL, ((ASTNodes.BoolType) node).arrExt);
//        } else if (node instanceof ASTNodes.CharType) {
//            return new VarType(VarType.PrimitiveType.CHAR, ((ASTNodes.CharType) node).arrExt);
//        } else if (node instanceof ASTNodes.IntType) {
//            return new VarType(VarType.PrimitiveType.INT, ((ASTNodes.IntType) node).arrExt);
//        } else if (node instanceof ASTNodes.FloatType) {
//            return new VarType(VarType.PrimitiveType.FLOAT, ((ASTNodes.FloatType) node).arrExt);
//        } else {
//            return new VarType(VarType.PrimitiveType.VOID, false);
//        }
//    }
//}
