package com.company.parser.abstract_syntax_tree;

import com.company.lexer.Token;
import com.company.parser.parse_tree.NodeType;
import com.company.parser.parse_tree.ParseTree.Node;
import com.company.parser.parse_tree.ParseTree;
import com.company.parser.abstract_syntax_tree.ASTNodes.ASTNode;

import java.beans.Expression;
import java.util.Objects;

public class AbstractSyntaxTree {

    public ASTNode root;

    private enum ExprType {
        MUL,
        ADD,
        OTHER
    }

    public static AbstractSyntaxTree from(ParseTree parseTree) {
        AbstractSyntaxTree abstractSyntaxTree = new AbstractSyntaxTree();
        abstractSyntaxTree.root = convertProgram(parseTree.root.firstChild);
        // Mirror expressionList tree so statements would generate in right order
        transformExpressionLists(abstractSyntaxTree.root);
        // Mirror expression tree so statements would generate in right order
        transformAllExpressions(abstractSyntaxTree.root);
        return abstractSyntaxTree;
    }

//    PROGRAM ::= DEFINITION FUNCTION_OR_BAG
//    PROGRAM ::= FUNCTION_OR_BAG
    private static ASTNode convertProgram(Node node) {
        Node first = node.firstChild;
        Node second = first.neighbor;
        var program = new ASTNodes.Program();

        //    PROGRAM ::= FUNCTION_OR_BAG
        if (second == null) {
            program.constructList = convertFunctionOrBag(first);
        }
        //    PROGRAM ::= DEFINITION FUNCTION FUNCTION_TAIL
        else {
            program.definition = convertDefinition(first);
            program.constructList = convertFunctionOrBag(second);
        }
        return program;
    }

//    DEFINITION ::= define { DEFINITION_ASSIGNMENT }
    private static ASTNode convertDefinition(Node node) {
        Node definitionAssignment = node.firstChild.neighbor.neighbor;
        ASTNodes.Definition definition = new ASTNodes.Definition();
        definition.firstDefinition = convertDefinitionAssignment(definitionAssignment);
        return definition;
    }

//    DEFINITION_ASSIGNMENT ::= PRIMITIVE_CONSTANT >> identifier ; DEFINITION_ASSIGNMENT
//    DEFINITION_ASSIGNMENT ::= ''
    private static ASTNode convertDefinitionAssignment(Node node) {
        Node first = node.firstChild;
        if (first == null) {
            return null;
        }
        Node identifier = first.neighbor.neighbor;
        Node definitionAssignment = identifier.neighbor.neighbor;

        ASTNodes.DefinitionInstance definitionInstance = new ASTNodes.DefinitionInstance();

        ASTNode primitiveConstant = convertPrimitiveConstant(first);
        definitionInstance.identifier = ((ParseTree.TerminalNode) identifier).value;
        definitionInstance.primitiveConstant = primitiveConstant;
        definitionInstance.nextDefinition = convertDefinitionAssignment(definitionAssignment);
        return definitionInstance;
    }

//    FUNCTION_OR_BAG ::= FUNCTION FUNCTION_OR_BAG
//    FUNCTION_OR_BAG ::= BAG_DEFINITION FUNCTION_OR_BAG
//    FUNCTION_OR_BAG ::= ''
    private static ASTNode convertFunctionOrBag(Node node) {
        Node first = node.firstChild;
        if (first == null) {
            return null;
        }

        Node second = first.neighbor;
        var constructList = new ASTNodes.ConstructList();
        if (first instanceof ParseTree.NonTerminalNode
                && ((ParseTree.NonTerminalNode) first).nodeType == NodeType.FUNCTION) {
            constructList.construct = convertFunctionDefinition(first);
        } else {
            constructList.construct = convertBagDefinition(first);
        }
        constructList.nextConstruct = convertFunctionOrBag(second);
        return constructList;
    }

//    BAG_DEFINITION ::= bag identifier { BAG_DEFINITION_PARAMETER_LIST }
    private static ASTNode convertBagDefinition(Node node) {
        var identifier = node.firstChild.neighbor;
        var bagDefinitionParameterList = identifier.neighbor.neighbor;

        var bagDefinition = new ASTNodes.BagDefinition();

        bagDefinition.bagName = ((ParseTree.TerminalNode) identifier).value;
        bagDefinition.paramList = convertBagDefinitionParameterList(bagDefinitionParameterList);

        return bagDefinition;
    }

//    BAG_DEFINITION_PARAMETER_LIST ::= identifier : TYPE BAG_DEFINITION_PARAMETER_LIST_TAIL
//    BAG_DEFINITION_PARAMETER_LIST ::= ''
//    BAG_DEFINITION_PARAMETER_LIST_TAIL ::= , identifier : TYPE BAG_DEFINITION_PARAMETER_LIST_TAIL
//    BAG_DEFINITION_PARAMETER_LIST_TAIL ::= ''
    private static ASTNode convertBagDefinitionParameterList(Node node) {
        var identifier = node.firstChild;

        if (identifier == null) {
            return null;
        }

        if (identifier instanceof ParseTree.TerminalNode
                && ((ParseTree.TerminalNode) identifier).tokenType == Token.TokenType.COMMA) {
            identifier = identifier.neighbor;
        }

        var type = identifier.neighbor.neighbor;
        var bagDefinitionParameterListTail = type.neighbor;

        var bagParameter = new ASTNodes.BagParam();
        bagParameter.identifier = ((ParseTree.TerminalNode) identifier).value;
        bagParameter.type = convertType(type);
        bagParameter.nextParam = convertBagDefinitionParameterList(bagDefinitionParameterListTail);

        return bagParameter;
    }

//    FUNCTION ::= fun identifier ( PARAM_LIST ) : RETURN_TYPE { STATEMENT_LIST }
    private static ASTNode convertFunctionDefinition(Node node) {
        var identifier = node.firstChild.neighbor;
        var paramList = identifier.neighbor.neighbor;
        var returnType = paramList.neighbor.neighbor.neighbor;
        var statementList = returnType.neighbor.neighbor;

        var functionDefinition = new ASTNodes.FunctionDefinition();
        functionDefinition.functionName = ((ParseTree.TerminalNode) identifier).value;
        functionDefinition.paramList = convertFunctionParamList(paramList);
        functionDefinition.returnType = convertType(returnType);
        functionDefinition.firstStatement = convertStatementList(statementList);

        return functionDefinition;
    }

//    PARAM_LIST ::= identifier : TYPE PARAM_LIST_TAIL
//    PARAM_LIST ::= ''
//
//    PARAM_LIST_TAIL ::= , identifier : TYPE PARAM_LIST_TAIL
//    PARAM_LIST_TAIL ::= ''
    private static ASTNode convertFunctionParamList(Node node) {
        Node first = node.firstChild;
        if (first == null) {
            return null;
        }
        if (((ParseTree.TerminalNode) first).value.equals(",")) {
            first = first.neighbor;
        }

        Node second = first.neighbor.neighbor;
        Node third = second.neighbor;

        ASTNodes.FunctionParam functionParam = new ASTNodes.FunctionParam();
        functionParam.identifier = ((ParseTree.TerminalNode)first).value;
        functionParam.type = convertType(second);
        functionParam.nextParam = convertFunctionParamList(third);
        return functionParam;
    }

//    STATEMENT_LIST ::= STATEMENT STATEMENT_LIST
//    STATEMENT_LIST ::= ''
    private static ASTNode convertStatementList(Node node) {
        ParseTree.NonTerminalNode first = (ParseTree.NonTerminalNode) node.firstChild;
        if (first == null) {
            return null;
        }
        Node second = first.neighbor;

        ASTNodes.Statement statement = new ASTNodes.Statement();

        first = (ParseTree.NonTerminalNode) first.firstChild;

        if (first.nodeType == NodeType.WHILE_STATEMENT) {
            statement.statement = convertWhileStatement(first);
        } else if (first.nodeType == NodeType.IF_STATEMENT) {
            statement.statement = convertIfStatement(first);
        } else if (first.nodeType == NodeType.BREAK_STATEMENT) {
            statement.statement = convertBreakStatement(first);
        } else if (first.nodeType == NodeType.CONTINUE_STATEMENT) {
            statement.statement = convertContinueStatement(first);
        } else if (first.nodeType == NodeType.EXPRESSION_LIST) {
            statement.statement = convertExpressionList(first);
        } else if (first.nodeType == NodeType.DEFINE_VAR) {
            statement.statement = convertDefineVar(first);
        } else if (first.nodeType == NodeType.INPUT_STATEMENT) {
            statement.statement = convertInputStatement(first);
        } else if (first.nodeType == NodeType.OUTPUT_STATEMENT){
            statement.statement = convertOutputStatement(first);
        } else if (first.nodeType == NodeType.ALLOC_ARR) {
            statement.statement = convertAllocArrStatement(first);
        } else if (first.nodeType == NodeType.FREE_ARR) {
            statement.statement = convertFreeStatement(first);
        } else if (first.nodeType == NodeType.RETURN_STATEMENT) {
            statement.statement = convertReturnStatement(first);
        } else {
            statement.statement = convertFillStatement(first);
        }

        statement.nextStatement = convertStatementList(second);

        return statement;
    }

//    FILL_STATEMENT ::= fill bag identifier >> ASSIGNABLE_INSTANCE ;
    private static ASTNode convertFillStatement(Node node) {
        var identifier = node.firstChild.neighbor.neighbor;
        var assignableInstance = identifier.neighbor.neighbor;

        var filLStatement = new ASTNodes.FillBag();
        filLStatement.bagName = ((ParseTree.TerminalNode) identifier).value;
        filLStatement.assignableInstance = convertAssignableInstance(assignableInstance);

        return filLStatement;
    }

//    DEFINE_VAR ::= let identifier : TYPE ;
    private static ASTNode convertDefineVar(Node node) {
        var identifier = node.firstChild.neighbor;
        var instantiateType = identifier.neighbor.neighbor;

        ASTNodes.DefineVar defineVar = new ASTNodes.DefineVar();
        defineVar.identifier = ((ParseTree.TerminalNode) identifier).value;
        defineVar.type = convertType(instantiateType);

        return defineVar;
    }

//    ALLOC_ARR ::= alloc PRIMITIVE_TYPE [ MOD_EXPR ] >> ASSIGNABLE_INSTANCE ;
    private static ASTNode convertAllocArrStatement(Node node) {
       var primitiveType = node.firstChild.neighbor;
       var expression = primitiveType.neighbor.neighbor;
       var assignableInstance = expression.neighbor.neighbor.neighbor;

       var allocArrStatement = new ASTNodes.AllocArr();
       allocArrStatement.type = convertPrimitiveType(primitiveType);
       allocArrStatement.expression = convertExpression(expression);
       allocArrStatement.assignableInstance = convertAssignableInstance(assignableInstance);

       return allocArrStatement;
    }

//    FREE_ARR ::= free ASSIGNABLE_INSTANCE ;
    private static ASTNode convertFreeStatement(Node node) {
        var assignableInstance = node.firstChild.neighbor;

        var freeArr = new ASTNodes.FreeInstance();
        freeArr.assignableInstance = convertAssignableInstance(assignableInstance);

        return freeArr;
    }

//    RETURN_STATEMENT ::= return ;
    private static ASTNode convertReturnStatement(Node node) {
        return new ASTNodes.Return();
    }

//    BREAK_STATEMENT ::= break ;
    private static ASTNode convertBreakStatement(Node node) {
        return new ASTNodes.Break();
    }

//    CONTINUE_STATEMENT ::= continue ;
    private static ASTNode convertContinueStatement(Node node) {
        return new ASTNodes.Continue();
    }

//    INPUT_STATEMENT ::= input ASSIGNABLE_INSTANCE ;
    private static ASTNode convertInputStatement(Node node) {
        var assignableInstance = node.firstChild.neighbor;

        var input = new ASTNodes.Input();

        input.assignableInstance = convertAssignableInstance(assignableInstance);

        return input;
    }

//    OUTPUT_STATEMENT ::= output EXPRESSION ;
    private static ASTNode convertOutputStatement(Node node) {
        Node expr = node.firstChild.neighbor;
        ASTNodes.Output output = new ASTNodes.Output();
        output.expression = convertExpression(expr);
        return output;
    }

//    WHILE_STATEMENT ::= while ( EXPRESSION ) { STATEMENT_LIST }
    private static ASTNode convertWhileStatement(Node node) {
        Node condition = node.firstChild.neighbor.neighbor;
        Node stmtList = condition.neighbor.neighbor.neighbor;

        ASTNodes.While whileStatement = new ASTNodes.While();
        whileStatement.condition = convertExpression(condition);
        whileStatement.firstStatement = convertStatementList(stmtList);

        return whileStatement;
    }

//    IF_STATEMENT ::= if ( EXPRESSION ) { STATEMENT_LIST } ELIF_STATEMENT ELSE_STATEMENT
//    ELIF_STATEMENT ::= elif ( EXPRESSION ) { STATEMENT_LIST } ELIF_STATEMENT
//    ELIF_STATEMENT ::= ''
//    ELSE_STATEMENT ::= else { STATEMENT_LIST }
//    ELSE_STATEMENT ::= ''
    private static ASTNode convertIfStatement(Node node) {
        ParseTree.NonTerminalNode nonTerminalNode = (ParseTree.NonTerminalNode) node;
        if (nonTerminalNode.nodeType == NodeType.IF_STATEMENT) {
            ASTNodes.If ifStatement = new ASTNodes.If();
            Node expr = node.firstChild.neighbor.neighbor;
            Node stmtList = expr.neighbor.neighbor.neighbor;
            Node elif = stmtList.neighbor.neighbor;
            Node els = elif.neighbor;

            ifStatement.condition = convertExpression(expr);
            ifStatement.firstStatement = convertStatementList(stmtList);
            ifStatement.elif = convertIfStatement(elif);

            ASTNodes.Elif currentElif = (ASTNodes.Elif) ifStatement.elif;
            if (currentElif == null) {
                ifStatement.elif = convertIfStatement(els);
                return ifStatement;
            }
            while (currentElif.elif != null) {
                currentElif = (ASTNodes.Elif) currentElif.elif;
            }
            currentElif.elif = convertIfStatement(els);

            return ifStatement;
        } else if (nonTerminalNode.nodeType == NodeType.ELIF_STATEMENT) {
            if (node.firstChild == null) {
                return null;
            }
            ASTNodes.Elif elifStatement = new ASTNodes.Elif();
            Node expr = node.firstChild.neighbor.neighbor;
            Node stmtList = expr.neighbor.neighbor.neighbor;
            Node elif = stmtList.neighbor.neighbor;

            elifStatement.condition = convertExpression(expr);
            elifStatement.firstStatement = convertStatementList(stmtList);
            elifStatement.elif = convertIfStatement(elif);

            return elifStatement;
        } else {
            if (node.firstChild == null) {
                return null;
            }
            ASTNodes.Else elseStatement = new ASTNodes.Else();
            Node stmtList = node.firstChild.neighbor.neighbor;
            elseStatement.firstStatement = convertStatementList(stmtList);

            return elseStatement;
        }
    }

//    EXPRESSION_LIST ::= EXPRESSION EXPRESSION_LIST_TAIL ;
//
//    EXPRESSION_LIST_TAIL ::= >> EXPRESSION_OR_CLOSER
//    EXPRESSION_LIST_TAIL ::= ''
//
//    EXPRESSION_OR_CLOSER ::= CLOSER
//    EXPRESSION_OR_CLOSER ::= EXPRESSION EXPRESSION_LIST_TAIL
    private static ASTNode convertExpressionList(Node node) {
        if (((ParseTree.NonTerminalNode)node).nodeType == NodeType.EXPRESSION_LIST) {
            ASTNode expr = convertExpression(node.firstChild);
            ASTNode nextExprList = convertExpressionList(node.firstChild.neighbor);
            ASTNodes.ExpressionList expressionList = new ASTNodes.ExpressionList();
            expressionList.nextExprList = nextExprList;
            expressionList.exprOrCloser = expr;
            return expressionList;
        } else if (((ParseTree.NonTerminalNode)node).nodeType == NodeType.EXPRESSION_LIST_TAIL) {
            if (node.firstChild == null) {
                return null;
            } else {
                return convertExpressionList(node.firstChild.neighbor);
            }
        } else {
            ParseTree.NonTerminalNode first = (ParseTree.NonTerminalNode) node.firstChild;
            ASTNodes.ExpressionList expressionList = new ASTNodes.ExpressionList();
            if (first.nodeType == NodeType.CLOSER) {
                expressionList.exprOrCloser = convertCloser(first);
            } else {
                expressionList.exprOrCloser = convertExpression(first);
                expressionList.nextExprList = convertExpressionList(first.neighbor);
            }
            return expressionList;
        }
    }

//    CLOSER ::= return
//    CLOSER ::= eq ASSIGNABLE_INSTANCE
    private static ASTNode convertCloser(Node node) {
        Node first = node.firstChild;
        if (((ParseTree.TerminalNode)first).tokenType == Token.TokenType.RETURN_KEYWORD) {
            return new ASTNodes.Return();
        } else {
            var assignableInstance = first.neighbor;

            var eq = new ASTNodes.Eq();
            eq.assignableInstance = convertAssignableInstance(assignableInstance);
            return eq;
        }
    }

//    EXPRESSION ::= MOD_EXPR EXPRESSION_TAIL
//    EXPRESSION_TAIL ::= >= MOD_EXPR
//    EXPRESSION_TAIL ::= <= MOD_EXPR
//    EXPRESSION_TAIL ::= < MOD_EXPR
//    EXPRESSION_TAIL ::= > MOD_EXPR
//    EXPRESSION_TAIL ::= == MOD_EXPR
//    EXPRESSION_TAIL ::= != MOD_EXPR
//    EXPRESSION_TAIL ::= ''
//
//    MOD_EXPR ::= ADD_EXPR MOD_EXPR_TAIL
//    MOD_EXPR_TAIL ::= % ADD_EXPR MOD_EXPR_TAIL
//    MOD_EXPR_TAIL ::= ''
//
//    ADD_EXPR ::= MUL_EXPR ADD_EXPR_TAIL
//    ADD_EXPR_TAIL ::= + MUL_EXPR ADD_EXPR_TAIL
//    ADD_EXPR_TAIL ::= - MUL_EXPR ADD_EXPR_TAIL
//    ADD_EXPR_TAIL ::= || MUL_EXPR ADD_EXPR_TAIL
//    ADD_EXPR_TAIL ::= ''
//
//    MUL_EXPR ::= PRIMARY MUL_EXPR_TAIL
//    MUL_EXPR_TAIL ::= * PRIMARY MUL_EXPR_TAIL
//    MUL_EXPR_TAIL ::= / PRIMARY MUL_EXPR_TAIL
//    MUL_EXPR_TAIL ::= && PRIMARY MUL_EXPR_TAIL
//    MUL_EXPR_TAIL ::= ''
    private static ASTNode convertExpression(Node node) {
        Node left = node.firstChild;
        if (left == null) {
            return null;
        }
        Node mid = left.neighbor;
        Node right = mid.neighbor;

        if (right == null) {
            if (((ParseTree.NonTerminalNode) node).nodeType == NodeType.EXPRESSION_TAIL) {
                return convertExpression(mid);
            } else {
                ASTNode convertedMidExpression = convertExpression(mid);
                if (convertedMidExpression == null) {
                    if (((ParseTree.NonTerminalNode) left).nodeType == NodeType.PRIMARY) {
                        return convertPrimary(left);
                    } else {
                        return convertExpression(left);
                    }
                } else {
                    ASTNodes.BinaryOperator expression = new ASTNodes.BinaryOperator();
                    expression.operator = ((ParseTree.TerminalNode) mid.firstChild).value;
                    expression.right = convertedMidExpression;
                    if (((ParseTree.NonTerminalNode) left).nodeType == NodeType.PRIMARY) {
                        expression.left = convertPrimary(left);
                    } else {
                        expression.left = convertExpression(left);
                    }
                    return expression;
                }
            }
        } else {
            ASTNode convertedRightExpression = convertExpression(right);
            if (convertedRightExpression == null) {
                if (((ParseTree.NonTerminalNode)mid).nodeType == NodeType.PRIMARY) {
                    return convertPrimary(mid);
                } else {
                    return convertExpression(mid);
                }
            } else {
                ASTNodes.BinaryOperator expression = new ASTNodes.BinaryOperator();
                expression.operator = ((ParseTree.TerminalNode) right.firstChild).value;
                expression.right = convertedRightExpression;
                if (((ParseTree.NonTerminalNode)mid).nodeType == NodeType.PRIMARY) {
                    expression.left = convertPrimary(mid);
                } else {
                    expression.left = convertExpression(mid);
                }
                return expression;
            }
        }
    }

//    PRIMARY ::= INSTANCE
//    PRIMARY ::= ! PRIMARY
//    PRIMARY ::= ( EXPRESSION )
//    PRIMARY ::= PRIMITIVE_CONSTANT
//    PRIMARY ::= in
    private static ASTNode convertPrimary(Node node) {
        Node left = node.firstChild;
        Node mid = left.neighbor;

        if (mid == null) {
            if (left.isTerminal) {
                return new ASTNodes.InKeyword();
            } else {
                if (((ParseTree.NonTerminalNode) left).nodeType == NodeType.PRIMITIVE_CONSTANT) {
                    return convertPrimitiveConstant(left);
                } else {
                    return convertInstance(left);
                }
            }
        }

        Node right = mid.neighbor;

        if (right == null) {
            ASTNodes.UnaryOperator not = new ASTNodes.UnaryOperator();
            not.operator = "!";
            not.left = convertPrimary(mid);
            return not;
        } else {
            return convertExpression(mid);
        }

    }

//    INSTANCE ::= identifier INSTANCE_TAIL
//    INSTANCE_TAIL ::= ( EXPR_LIST )
//    INSTANCE_TAIL ::= ARRAY_EXTENSION

//    INSTANCE ::= identifier INSTANCE_TAIL
//    INSTANCE_TAIL ::= ( EXPR_LIST )
//    INSTANCE_TAIL ::= ASSIGNABLE_INSTANCE_TAIL
    private static ASTNode convertInstance(Node node) {
        Node identifier = node.firstChild;
        Node instanceTail = identifier.neighbor;
        Node tailChild = instanceTail.firstChild;
        if (tailChild.isTerminal) {
            // EXPR_LIST
            ASTNodes.FunctionCall functionCall =  new ASTNodes.FunctionCall();
            functionCall.identifier = ((ParseTree.TerminalNode) identifier).value;
            functionCall.firstArgument = convertFunctionCall(tailChild.neighbor);
            return functionCall;
        } else {
            var variable = new ASTNodes.Variable();
            variable.name = ((ParseTree.TerminalNode) identifier).value;
            variable.callExtension = convertAssignableInstanceTail(tailChild);

            return variable;
        }
    }

//    ASSIGNABLE_INSTANCE ::= identifier ASSIGNABLE_INSTANCE_TAIL
    private static ASTNode convertAssignableInstance(Node node) {
        var identifier = node.firstChild;
        var assignableInstanceTail = identifier.neighbor;

        var assignableInstance = new ASTNodes.Variable();
        assignableInstance.name = ((ParseTree.TerminalNode) identifier).value;
        assignableInstance.callExtension = convertAssignableInstanceTail(assignableInstanceTail);

        return assignableInstance;
    }

//    ASSIGNABLE_INSTANCE_TAIL ::= [ MOD_EXPR ] DOT_TAIL
//    ASSIGNABLE_INSTANCE_TAIL ::= DOT_TAIL
    private static ASTNode convertAssignableInstanceTail(Node node) {
        var first = node.firstChild;

        if (first instanceof ParseTree.TerminalNode) {
            var expression = first.neighbor;
            var dotTail = expression.neighbor.neighbor;

            var arrayCallExtension = new ASTNodes.ArrayCallExtension();
            arrayCallExtension.expression = convertExpression(expression);
            arrayCallExtension.callExtension = convertDotTail(dotTail);

            return arrayCallExtension;
        } else {
            return convertDotTail(first);
        }
    }

//    DOT_TAIL ::= . identifier ASSIGNABLE_INSTANCE_TAIL
//    DOT_TAIL ::= ''
    private static ASTNode convertDotTail(Node node) {
        var first = node.firstChild;

        if (first == null) {
            return null;
        }

        var identifier = first.neighbor;
        var assignableInstanceTail = identifier.neighbor;

        var bagCallExtension = new ASTNodes.BagCallExtension();
        bagCallExtension.fieldName = ((ParseTree.TerminalNode) identifier).value;
        bagCallExtension.callExtension = convertAssignableInstanceTail(assignableInstanceTail);

        return bagCallExtension;
    }

//    EXPR_LIST ::= EXPRESSION EXPR_LIST_TAIL
//    EXPR_LIST ::= ''
    private static ASTNode convertFunctionCall(Node node) {
        Node first = node.firstChild;
        if (first == null) {
            return null;
        }
        var argument = new ASTNodes.FunctionCallArgument();
        argument.expression = convertExpression(first);
        argument.next = convertFunctionCallTail(first.neighbor);
        return argument;
    }

//    EXPR_LIST_TAIL ::= , EXPRESSION EXPR_LIST_TAIL
//    EXPR_LIST_TAIL ::= ''
    private static ASTNode convertFunctionCallTail(Node node) {
        Node first = node.firstChild;
        if (first == null) {
            return null;
        }

        var argument = new ASTNodes.FunctionCallArgument();
        argument.expression = convertExpression(first.neighbor);
        argument.next = convertFunctionCallTail(first.neighbor.neighbor);

        return argument;
    }

//    RETURN_TYPE ::= TYPE
//    RETURN_TYPE ::= void
//
//    TYPE ::= PRIMITIVE_TYPE ARRAY_EMPTY_EXTENSION
    private static ASTNode convertType(Node node) {
        var typeNode = node;
        if (((ParseTree.NonTerminalNode)node).nodeType == NodeType.RETURN_TYPE) {
            if (node.firstChild instanceof ParseTree.TerminalNode) {
                // RETURN_TYPE ::= void
                return new ASTNodes.VoidType();
            }
            // RETURN_TYPE ::= TYPE
            typeNode = node.firstChild;
        }

        // TYPE ::= PRIMITIVE_TYPE ARRAY_EMPTY_EXTENSION

        var primitiveType = typeNode.firstChild;

//        ARRAY_EMPTY_EXTENSION ::= [ ]
//        ARRAY_EMPTY_EXTENSION ::= ''
        var arrEmptyExt = primitiveType.neighbor.firstChild;
        boolean hasArrExt = arrEmptyExt != null;

        var type = (ASTNodes.Type) convertPrimitiveType(primitiveType);

        type.hasArrayExtension = hasArrExt;
        return type;
    }

//    PRIMITIVE_TYPE ::= int
//    PRIMITIVE_TYPE ::= float
//    PRIMITIVE_TYPE ::= char
//    PRIMITIVE_TYPE ::= bool
//    PRIMITIVE_TYPE ::= bag identifier
    private static ASTNode convertPrimitiveType(Node node) {
        var primitiveTypeName = (ParseTree.TerminalNode) node.firstChild;

        if (primitiveTypeName.tokenType == Token.TokenType.BOOL_KEYWORD) {
            // PRIMITIVE_TYPE ::= bool
            return new ASTNodes.BoolType();
        } else if (primitiveTypeName.tokenType == Token.TokenType.CHAR_KEYWORD) {
            // PRIMITIVE_TYPE ::= char
            return new ASTNodes.CharType();
        } else if (primitiveTypeName.tokenType == Token.TokenType.INT_KEYWORD) {
            // PRIMITIVE_TYPE ::= int
            return new ASTNodes.IntType();
        } else if (primitiveTypeName.tokenType == Token.TokenType.FLOAT_KEYWORD) {
            // PRIMITIVE_TYPE ::= float
            return new ASTNodes.FloatType();
        } else {
            // PRIMITIVE_TYPE ::= bag identifier
            var primitiveType = new ASTNodes.BagType();
            primitiveType.bagName = ((ParseTree.TerminalNode) primitiveTypeName.neighbor).value;

            return primitiveType;
        }
    }

//    PRIMITIVE_CONSTANT ::= char_constant
//    PRIMITIVE_CONSTANT ::= bool_constant
//    PRIMITIVE_CONSTANT ::= int_constant
//    PRIMITIVE_CONSTANT ::= float_constant
    private static ASTNode convertPrimitiveConstant(Node node) {
        ParseTree.TerminalNode terminalNode = (ParseTree.TerminalNode) node.firstChild;
        if (terminalNode.tokenType == Token.TokenType.CHAR_CONSTANT) {
            ASTNodes.CharConstant charConstant = new ASTNodes.CharConstant();
            charConstant.value = terminalNode.value;
            return charConstant;
        } else if (terminalNode.tokenType == Token.TokenType.BOOL_CONSTANT) {
            ASTNodes.BoolConstant boolConstant = new ASTNodes.BoolConstant();
            boolConstant.value = terminalNode.value;
            return boolConstant;
        } else if (terminalNode.tokenType == Token.TokenType.INT_CONSTANT) {
            ASTNodes.IntConstant intConstant = new ASTNodes.IntConstant();
            intConstant.value = terminalNode.value;
            return intConstant;
        } else {
            ASTNodes.FloatConstant floatConstant = new ASTNodes.FloatConstant();
            floatConstant.value = terminalNode.value;
            return floatConstant;
        }
    }

    private static ASTNodes.ExpressionList transformSingleExpressionList(ASTNodes.ExpressionList node) {
        if (node.nextExprList == null) {
            return node;
        }
        ASTNodes.ExpressionList tmp = transformSingleExpressionList((ASTNodes.ExpressionList) node.nextExprList);
        ((ASTNodes.ExpressionList) node.nextExprList).nextExprList = node;
        node.nextExprList = null;
        return tmp;
    }

    public static void transformExpressionLists(ASTNode node) {
        if (node instanceof ASTNodes.Statement && ((ASTNodes.Statement) node).statement instanceof ASTNodes.ExpressionList) {
            ((ASTNodes.Statement) node).statement =
                    transformSingleExpressionList((ASTNodes.ExpressionList) ((ASTNodes.Statement) node).statement);
            if (((ASTNodes.Statement) node).nextStatement != null) {
                transformExpressionLists(((ASTNodes.Statement) node).nextStatement);
            }
        } else {
            node
                    .getChildren()
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(AbstractSyntaxTree::transformExpressionLists);
        }
    }
    private static ExprType isMulOrDiv(ASTNodes.BinaryOperator operator) {
        if (operator.operator.equals("*") || operator.operator.equals("/")) {
            return ExprType.MUL;
        } else if (operator.operator.equals("+") || operator.operator.equals("-")) {
            return ExprType.ADD;
        } else {
            return ExprType.OTHER;
        }
    }

    private static boolean shouldTransform(ASTNode node) {
        return node instanceof ASTNodes.BinaryOperator op && op.right instanceof ASTNodes.BinaryOperator childOp &&
                isMulOrDiv(op) == isMulOrDiv(childOp) && isMulOrDiv(op) != ExprType.OTHER;
    }

    private static ASTNode transformAllExpressions(ASTNode node) {
        if (node == null) {
            return null;
        }

        if (node instanceof ASTNodes.Output output) {
            output.expression = transformAllExpressions(output.expression);
        } else if (node instanceof ASTNodes.While whileNode) {
            whileNode.condition = transformAllExpressions(whileNode.condition);
            transformAllExpressions(whileNode.firstStatement);
        } else if (node instanceof ASTNodes.If ifNode) {
            ifNode.condition = transformAllExpressions(ifNode.condition);
            transformAllExpressions(ifNode.firstStatement);
            transformAllExpressions(ifNode.elif);
        } else if (node instanceof ASTNodes.Elif elif) {
            elif.condition = transformAllExpressions(elif.condition);
            transformAllExpressions(elif.firstStatement);
            transformAllExpressions(elif.elif);
        } else if (node instanceof ASTNodes.ExpressionList exprList && exprList.exprOrCloser instanceof ASTNodes.BinaryOperator expr) {
            exprList.exprOrCloser = transformAllExpressions(expr);
            transformAllExpressions(exprList.nextExprList);
        } else if (node instanceof ASTNodes.BinaryOperator op) {
            if (op.right instanceof ASTNodes.BinaryOperator rightOp && isMulOrDiv(op) != isMulOrDiv(rightOp)) {
                op.right = transformAllExpressions(rightOp);
            } else if (shouldTransform(op)) {
                ASTNodes.BinaryOperator rightOp = (ASTNodes.BinaryOperator) op.right;
                ASTNode result = transformAllExpressions(rightOp);
                op.right = rightOp.left;
                rightOp.left = op;
                op.left = transformAllExpressions(op.left);
                return result;
            } else {
                transformAllExpressions(op.right);
            }
            op.left = transformAllExpressions(op.left);
        } else {
            for (ASTNode child : node.getChildren()) {
                transformAllExpressions(child);
            }
        }

        return node;
    }

    public void print() {
        root.print(0);
    }
}
