package com.company.parser.abstract_syntax_tree.helpers;

import com.company.dev_exceptions.TypeToTypeConversionException;
import com.company.parser.abstract_syntax_tree.ASTNodes;
import com.company.symbol_table.variable_types.*;

import java.util.function.Function;

public class TypeToTypeConverter implements Function<ASTNodes.TypeOrVoid, VarType> {
    @Override
    public VarType apply(ASTNodes.TypeOrVoid node) {
        return switch (node) {
            case ASTNodes.BoolType boolType ->
                    new BoolType(boolType.hasArrayExtension);
            case ASTNodes.CharType charType ->
                    new CharType(charType.hasArrayExtension);
            case ASTNodes.IntType intType ->
                    new IntType(intType.hasArrayExtension);
            case ASTNodes.FloatType floatType ->
                    new FloatType(floatType.hasArrayExtension);
            case ASTNodes.VoidType _ ->
                    new VoidType();
            case ASTNodes.BagType bagType ->
                    new BagType(bagType.bagName, bagType.hasArrayExtension);
            default -> null;
        };
    }
}
