package com.company.old.parser.abstract_syntax_tree;

import com.company.old.symbol_table.SymbolTable;
import com.company.old.symbol_table.variable_types.VarType;

import java.util.Arrays;
import java.util.List;

public class ASTNodes {
    public abstract static class ASTNode {
        public int time = -1;
        public int scope = -1;
        public ASTNode parent;
        public VarType returnResultType;
        public int line;

        public abstract List<ASTNode> getChildren();

        public String getNodeType() {
            return this.getClass().getSimpleName();
        }

        public String getName() {
            return null;
        }

        public void print(int n) {
            System.out.printf("scope: %d, time %d, line: %d - ", scope, time, line);
            System.out.printf("%d ", n);
            for (int i = 0; i < n; i++) {
                System.out.print("-");
            }
            String name = this.getName();
            if (name != null) {
                System.out.printf("%s (%s)\n", this.getNodeType(), name);
            } else {
                System.out.println(this.getNodeType());
            }

            for (ASTNode child : this.getChildren()) {
                if (child != null) {
                    child.print(n + 1);
                }
            }
        }
    }

    public static class Program extends ASTNode {
        public ASTNode definition;
        public ASTNode constructList;

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(definition, constructList);
        }
    }

    public static class Definition extends ASTNode {
        public ASTNode firstDefinition;

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(firstDefinition);
        }
    }

    public static class DefinitionInstance extends ASTNode {
        public ASTNode primitiveConstant;
        public String identifier;
        public ASTNode nextDefinition;

        @Override
        public String getName() {
            return identifier;
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(primitiveConstant, nextDefinition);
        }
    }

    public static class ConstructList extends ASTNode {
        public ASTNode construct;
        public ASTNode nextConstruct;

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(construct, nextConstruct);
        }
    }

    public static class FunctionDefinition extends ASTNode {
        public String functionName;
        public ASTNode paramList;
        public ASTNode returnType;
        public ASTNode firstStatement;

        @Override
        public String getName() {
            return functionName;
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(paramList, returnType, firstStatement);
        }
    }

    public static class BagDefinition extends ASTNode {
        public String bagName;
        public ASTNode paramList;

        @Override
        public String getName() {
            return bagName;
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(paramList);
        }
    }

    public static class FunctionParam extends ASTNode {
        public String identifier;
        public ASTNode type;
        public ASTNode nextParam;

        @Override
        public String getName() {
            return identifier;
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(type, nextParam);
        }
    }

    public static class BagParam extends ASTNode {
        public String identifier;
        public ASTNode type;
        public ASTNode nextParam;

        @Override
        public String getName() {
            return identifier;
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(type, nextParam);
        }
    }

    public static class Statement extends ASTNode {
        public ASTNode statement;
        public ASTNode nextStatement;

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(statement, nextStatement);
        }
    }

    public static class While extends ASTNode {
        public ASTNode condition;
        public ASTNode firstStatement;

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(condition, firstStatement);
        }
    }

    public static class If extends ASTNode {
        public ASTNode condition;
        public ASTNode firstStatement;
        public ASTNode elif;

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(condition, firstStatement, elif);
        }
    }

    public static class Elif extends ASTNode {
        public ASTNode condition;
        public ASTNode firstStatement;
        public ASTNode elif;

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(condition, firstStatement, elif);
        }
    }

    public static class Else extends ASTNode {
        public ASTNode firstStatement;

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(firstStatement);
        }
    }

    public static class Eq extends ASTNode {
        public ASTNode assignableInstance;

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(assignableInstance);
        }
    }

    public static class Return extends ASTNode {
        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList();
        }
    }

    public static class Break extends ASTNode {
        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList();
        }
    }

    public static class Continue extends ASTNode {
        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList();
        }
    }

    public static class DefineVar extends ASTNode {
        public String identifier;
        public ASTNode type;

        @Override
        public String getName() {
            return identifier;
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(type);
        }
    }

    public static class AllocArr extends ASTNode {
        public ASTNode type;
        public ASTNode expression;
        public ASTNode assignableInstance;

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(type, expression, assignableInstance);
        }
    }

    public static class FillBag extends ASTNode {
        public String bagName;
        public ASTNode firstFillBagArgument;
        public ASTNode assignableInstance;

        @Override
        public String getName() {
            return bagName;
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(firstFillBagArgument, assignableInstance);
        }
    }

    public static class FreeInstance extends ASTNode {
        public ASTNode assignableInstance;

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(assignableInstance);
        }
    }

    public static class Output extends ASTNode {
        public ASTNode expression;

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(expression);
        }
    }

    public static class Input extends ASTNode {
        public ASTNode assignableInstance;

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(assignableInstance);
        }
    }

    public static class InKeyword extends ASTNode {
        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList();
        }
    }

    public static class ExpressionList extends ASTNode {
        public ASTNode exprOrCloser;
        public ASTNode nextExprList;

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(exprOrCloser, nextExprList);
        }
    }

    public static class BinaryOperator extends ASTNode {
        public String operator;
        public ASTNode left;
        public ASTNode right;

        @Override
        public String getName() {
            return operator;
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(left, right);
        }
    }

    public static class UnaryOperator extends ASTNode {
        public String operator;
        public ASTNode left;

        @Override
        public String getName() {
            return operator;
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(left);
        }
    }

    public static class Variable extends ASTNode {
        public String name;
        public ASTNode callExtension;
        public SymbolTable.VarOrConstInfo info;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(callExtension);
        }
    }

    public static class ArrayCallExtension extends ASTNode {
        public ASTNode expression;
        public ASTNode callExtension;

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(expression, callExtension);
        }
    }

    public static class BagCallExtension extends ASTNode {
        public String fieldName;
        public ASTNode callExtension;

        @Override
        public String getName() {
            return fieldName;
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(callExtension);
        }
    }

    public static class FunctionCall extends ASTNode {
        public String identifier;
        public ASTNode firstArgument;

        @Override
        public String getName() {
            return identifier;
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(firstArgument);
        }
    }

    public static class FunctionCallArgument extends ASTNode {
        public ASTNode expression;
        public ASTNode next;

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList(expression, next);
        }
    }

    public static abstract class Constant extends ASTNode {
        public String value;

        @Override
        public String getName() {
            return value;
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList();
        }
    }

    public static class BoolConstant extends Constant {
    }

    public static class CharConstant extends Constant {
    }

    public static class IntConstant extends Constant {
    }

    public static class FloatConstant extends Constant {
    }

    public static abstract class TypeOrVoid extends ASTNode {
    }

    public static abstract class Type extends TypeOrVoid {
        public boolean hasArrayExtension;

        @Override
        public String getNodeType() {
            return this.getClass().getSimpleName() + (hasArrayExtension ? "[]" : "");
        }

        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList();
        }
    }

    public static class CharType extends Type {
    }

    public static class BoolType extends Type {
    }

    public static class IntType extends Type {
    }

    public static class FloatType extends Type {
    }

    public static class BagType extends Type {
        public String bagName;

        @Override
        public String getName() {
            return bagName;
        }
    }

    public static class VoidType extends TypeOrVoid {
        @Override
        public List<ASTNode> getChildren() {
            return Arrays.asList();
        }
    }
}
