package com.company.parser.abstract_syntax_tree;

import com.company.symbol_table.SymbolTable;
import com.company.symbol_table.VarType;

import java.util.Arrays;
import java.util.List;

public class ASTNodes {
    public abstract static class ASTNode {
        public int time = -1;
        public int scope = -1;
        public ASTNode parent;
        public VarType returnResultType;

        abstract public String getName();
        abstract public void print(int n);
        public abstract List<ASTNode> getChildren();
    }

    public static class Program extends ASTNode {
        public ASTNode definition;
        public ASTNode firstFunction;

        @Override
        public String getName() {
            return "Program";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(definition, firstFunction);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("Program");

            if (definition != null) {
                definition.print(n + 1);
            }

            if (firstFunction != null) {
                firstFunction.print(n + 1);
            }
        }
    }

    public static class FunctionList extends ASTNode {
        public ASTNode function;
        public ASTNode nextFunction;

        @Override
        public String getName() {
            return "FunctionList";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(function, nextFunction);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("FunctionList");

            if (function != null) {
                function.print(n + 1);
            }

            if (nextFunction != null) {
                nextFunction.print(n + 1);
            }
        }
    }

    public static class Function extends ASTNode {
        public String name;
        public ASTNode paramList;
        public ASTNode returnType;
        public ASTNode firstStatement;

        @Override
        public String getName() {
            return String.format("Function('name': %s)\n", name);
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(paramList, returnType, firstStatement);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("Function: %s\n", name);

            if (paramList != null) {
                paramList.print(n + 1);
            }

            if (returnType != null) {
                returnType.print(n + 1);
            }

            if (firstStatement != null) {
                firstStatement.print(n + 1);
            }
        }
    }

    public static class Definition extends ASTNode {
        public ASTNode firstDefinition;

        @Override
        public String getName() {
            return "Definition";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(firstDefinition);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("Definition");

            if (firstDefinition != null) {
                firstDefinition.print(n + 1);
            }
        }
    }

    public static class DefinitionInstance extends ASTNode {
        public ASTNode primitiveConstant;
        public String identifier;
        public ASTNode nextDefinition;

        @Override
        public String getName() {
            return String.format("DefinitionInstance defining %s", identifier);
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(primitiveConstant, nextDefinition);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("DefinitionInstance defining %s\n", identifier);

            if (primitiveConstant != null) {
                primitiveConstant.print(n + 1);
            }

            if (nextDefinition != null) {
                nextDefinition.print(n + 1);
            }
        }
    }

    public static class FunctionParam extends ASTNode {
        public String identifier;
        public ASTNode type;
        public ASTNode nextParam;

        @Override
        public String getName() {
            return "FunctionParam";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(type, nextParam);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("FunctionParam: %s\n", identifier);

            if (type != null) {
                type.print(n + 1);
            }

            if (nextParam != null) {
                nextParam.print(n + 1);
            }
        }
    }

    public static class While extends ASTNode {
        public ASTNode condition;
        public ASTNode firstStatement;

        @Override
        public String getName() {
            return "While";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(condition, firstStatement);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("While");

            if (condition != null) {
                condition.print(n + 1);
            }

            if (firstStatement != null) {
                firstStatement.print(n + 1);
            }
        }
    }

    public static class If extends ASTNode {
        public ASTNode condition;
        public ASTNode firstStatement;
        public ASTNode elif;

        @Override
        public String getName() {
            return "If";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(condition, firstStatement, elif);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("If");

            if (condition != null) {
                condition.print(n + 1);
            }

            if (firstStatement != null) {
                firstStatement.print(n + 1);
            }

            if (elif != null) {
                elif.print(n + 1);
            }
        }
    }

    public static class Elif extends ASTNode {
        public ASTNode condition;
        public ASTNode firstStatement;
        public ASTNode elif;

        @Override
        public String getName() {
            return "Elif";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(condition, firstStatement, elif);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("Elif");

            if (condition != null) {
                condition.print(n + 1);
            }

            if (firstStatement != null) {
                firstStatement.print(n + 1);
            }

            if (elif != null) {
                elif.print(n + 1);
            }
        }
    }

    public static class Else extends ASTNode {
        public ASTNode firstStatement;

        @Override
        public String getName() {
            return "Else";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(firstStatement);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("Else");

            if (firstStatement != null) {
                firstStatement.print(n + 1);
            }
        }
    }

    public static class ExpressionList extends ASTNode {
        public ASTNode exprOrCloser;
        public ASTNode nextExprList;

        @Override
        public String getName() {
            return "ExpressionList";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(nextExprList, exprOrCloser);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("ExpressionList");

            if (nextExprList != null) {
                nextExprList.print(n + 1);
            }

            if (exprOrCloser != null) {
                exprOrCloser.print(n + 1);
            }
        }
    }

    public static class Statement extends ASTNode {
        public ASTNode statement;
        public ASTNode nextStatement;

        @Override
        public String getName() {
            return "Statement";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(statement, nextStatement);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("Statement");

            if (statement != null) {
                statement.print(n + 1);
            }

            if (nextStatement != null) {
                nextStatement.print(n + 1);
            }
        }
    }

    public static class BinaryOperator extends ASTNode {
        public String value;
        public ASTNode left;
        public ASTNode right;

        @Override
        public String getName() {
            return "BinaryOperator";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(left, right);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("BinaryOperator: %s\n", value);

            if (left != null) {
                left.print(n + 1);
            }

            if (right != null) {
                right.print(n + 1);
            }
        }
    }

    public static class UnaryOperator extends ASTNode {
        public String value;
        public ASTNode left;

        @Override
        public String getName() {
            return "UnaryOperator";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(left);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("UnaryOperator: %s\n", value);

            if (left != null) {
                left.print(n + 1);
            }
        }
    }

    public static class Eq extends ASTNode {
        public String identifier;
        public ASTNode arrCall;

        @Override
        public String getName() {
            return "Eq";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(arrCall);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("Eq: %s\n", identifier);
            if (arrCall != null) {
                arrCall.print(n + 1);
            }
        }
    }

    public static class Return extends ASTNode {

        @Override
        public String getName() {
            return "Return";
        }

        @Override
        public List<ASTNode> getChildren() {
            return List.of();
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("Return");
        }
    }

    public static class Break extends ASTNode {

        @Override
        public String getName() {
            return "Break";
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("Break");
        }

        @Override
        public List<ASTNode> getChildren() {
            return List.of();
        }
    }

    public static class Continue extends ASTNode {

        @Override
        public String getName() {
            return "Continue";
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("Continue");
        }

        @Override
        public List<ASTNode> getChildren() {
            return List.of();
        }
    }

    public static class DefineVar extends ASTNode {
        public String identifier;
        public ASTNode type;

        @Override
        public String getName() {
            return "DefineVar";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(type);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("DefineVar: %s\n", identifier);

            if (type != null) {
                type.print(n + 1);
            }
        }
    }

    public static class AllocArr extends ASTNode {
        public String identifier;
        public ASTNode allocArrType;

        @Override
        public String getName() {
            return "AllocArr";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(allocArrType);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("AllocArr: %s\n", identifier);

            if (allocArrType != null) {
                allocArrType.print(n + 1);
            }
        }
    }

    public static class FreeArr extends ASTNode {
        public String identifier;

        @Override
        public String getName() {
            return "FreeArr";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList();
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("FreeArr: %s\n", identifier);
        }
    }

    public static class Output extends ASTNode {
        public ASTNode node;

        @Override
        public String getName() {
            return "Output";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(node);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("Output");

            if (node != null) {
                node.print(n + 1);
            }
        }
    }

    public static class Input extends ASTNode {
        public ASTNode identifier;

        @Override
        public String getName() {
            return "Input";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(identifier);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("Input");

            if (identifier != null) {
                identifier.print(n + 1);
            }
        }
    }

    public static class InKeyword extends ASTNode {

        @Override
        public String getName() {
            return "InKeyword";
        }

        @Override
        public List<ASTNode> getChildren() {
            return List.of();
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("InKeyword");
        }
    }

    public static class Identifier extends ASTNode {
        public String value;
        public SymbolTable.VarOrConstInfo info;

        @Override
        public String getName() {
            return "Identifier";
        }

        @Override
        public List<ASTNode> getChildren() {
            return List.of();
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("Identifier: %s\n", value);
        }
    }

    public static class ArrayCall extends ASTNode {
        public String identifier;
        public ASTNode callIdx;

        @Override
        public String getName() {
            return "ArrayCall";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(callIdx);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("ArrayCall: %s\n", identifier);

            if (callIdx != null) {
                callIdx.print(n + 1);
            }
        }
    }

    public static class ArrayCallIdx extends ASTNode {
        public ASTNode expression;

        @Override
        public String getName() {
            return "ArrayCallIdx";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(expression);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("ArrayCallIdx");

            if (expression != null) {
                expression.print(n + 1);
            }
        }
    }

    public static class FunctionCall extends ASTNode {
        public String identifier;
        public ASTNode firstChild;

        @Override
        public String getName() {
            return "FunctionCall";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(firstChild);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("FunctionCall: %s\n", identifier);

            if (firstChild != null) {
                firstChild.print(n + 1);
            }
        }
    }

    public static class FunctionCallIdx extends ASTNode {
        public ASTNode expression;
        public ASTNode next;

        @Override
        public String getName() {
            return "FunctionCallIdx";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(expression, next);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("FunctionCallIdx");

            if (expression != null) {
                expression.print(n + 1);
            }

            if (next != null) {
                next.print(n + 1);
            }
        }
    }

    public static abstract class Constant extends ASTNode {
        public String value;
    }

    public static class BoolConstant extends Constant {
        @Override
        public String getName() {
            return "BoolConstant";
        }

        @Override
        public List<ASTNode> getChildren() {
            return List.of();
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("BoolConstant: %s\n", value);
        }
    }

    public static class CharConstant extends Constant {
        @Override
        public String getName() {
            return "CharConstant";
        }

        @Override
        public List<ASTNode> getChildren() {
            return List.of();
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("CharConstant: %s\n", value);
        }
    }

    public static class IntConstant extends Constant {
        @Override
        public String getName() {
            return "IntConstant";
        }

        @Override
        public List<ASTNode> getChildren() {
            return List.of();
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("IntConstant: %s\n", value);
        }
    }

    public static class FloatConstant extends Constant {
        @Override
        public String getName() {
            return "FloatConstant";
        }

        @Override
        public List<ASTNode> getChildren() {
            return List.of();
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("FloatConstant: %s\n", value);
        }
    }

    public static class CharType extends ASTNode {
        public boolean arrExt;

        @Override
        public String getName() {
            return "CharType";
        }

        @Override
        public List<ASTNode> getChildren() {
            return List.of();
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("CharType %s", arrExt ? " []" : "");
        }
    }

    public static class BoolType extends ASTNode {
        public boolean arrExt;

        @Override
        public String getName() {
            return "BoolType";
        }

        @Override
        public List<ASTNode> getChildren() {
            return List.of();
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("BoolType%s\n", arrExt ? " []" : "");
        }
    }

    public static class IntType extends ASTNode {
        public boolean arrExt;

        @Override
        public String getName() {
            return "IntType";
        }

        @Override
        public List<ASTNode> getChildren() {
            return List.of();
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("IntType%s\n", arrExt ? " []" : "");
        }
    }

    public static class FloatType extends ASTNode {
        public boolean arrExt;

        @Override
        public String getName() {
            return "FloatType";
        }

        @Override
        public List<ASTNode> getChildren() {
            return List.of();
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.printf("FloatType%s\n", arrExt ? " []" : "");
        }
    }

    public static abstract class InitType extends ASTNode {
        public ASTNode arrExt;
    }

    public static class CharInitType extends InitType {
        @Override
        public String getName() {
            return "CharInitType";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(arrExt);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("CharInitType");

            if (arrExt != null) {
                arrExt.print(n + 1);
            }
        }
    }

    public static class BoolInitType extends InitType {
        @Override
        public String getName() {
            return "BoolInitType";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(arrExt);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("BoolInitType");

            if (arrExt != null) {
                arrExt.print(n + 1);
            }
        }
    }

    public static class IntInitType extends InitType {
        @Override
        public String getName() {
            return "IntInitType";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(arrExt);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("IntInitType");

            if (arrExt != null) {
                arrExt.print(n + 1);
            }
        }
    }

    public static class FloatInitType extends InitType {
        @Override
        public String getName() {
            return "FloatInitType";
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(arrExt);
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("FloatInitType");

            if (arrExt != null) {
                arrExt.print(n + 1);
            }
        }
    }

    public static class VoidType extends ASTNode {

        @Override
        public String getName() {
            return "VoidType";
        }

        @Override
        public List<ASTNode> getChildren() {
            return List.of();
        }

        @Override
        public void print(int n) {
            System.out.printf("scope: %d, time %d - ", scope, time);
            System.out.printf("%d ", n);
            for (int i = 0 ; i < n ; i++) {
                System.out.print("-");
            }
            System.out.println("VoidType");
        }
    }

}
