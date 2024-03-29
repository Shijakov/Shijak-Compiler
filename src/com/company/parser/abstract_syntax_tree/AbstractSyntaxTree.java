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
        transformExpressionLists(abstractSyntaxTree.root);
        transformAllExpressions(abstractSyntaxTree.root);
        return abstractSyntaxTree;
    }

//    PROGRAM ::= DEFINITION FUNCTION FUNCTION_TAIL
//    PROGRAM ::= FUNCTION FUNCTION_TAIL
    private static ASTNode convertProgram(Node node) {
        Node first = node.firstChild;
        Node second = first.neighbor;
        Node third = second.neighbor;
        ASTNodes.Program program = new ASTNodes.Program();
        ASTNodes.FunctionList functionList = new ASTNodes.FunctionList();
        //    PROGRAM ::= FUNCTION FUNCTION_TAIL
        if (third == null) {
            functionList.function = convertFunction(first);
            functionList.nextFunction = convertFunctionList(second);
        }
        //    PROGRAM ::= DEFINITION FUNCTION FUNCTION_TAIL
        else {
            program.definition = convertDefinition(first);
            functionList.function = convertFunction(second);
            functionList.nextFunction = convertFunctionList(third);
        }
        program.firstFunction = functionList;
        return program;
    }

//    FUNCTION_TAIL ::= FUNCTION FUNCTION_TAIL
//    FUNCTION_TAIL ::= ''
    private static ASTNode convertFunctionList(Node node) {
        Node first = node.firstChild;
        if (first == null) {
            return null;
        }

        Node second = first.neighbor;
        ASTNodes.FunctionList functionList = new ASTNodes.FunctionList();
        functionList.function = convertFunction(first);
        functionList.nextFunction = convertFunctionList(second);
        return functionList;
    }

//    DEFINITION ::= define { DEFINITION_ASSIGNMENT }
    private static ASTNode convertDefinition(Node node) {
        Node definitionAssignment = node.firstChild.neighbor.neighbor;
        ASTNodes.Definition definition = new ASTNodes.Definition();
        definition.firstDefinition = convertDefinitionAssignmentList(definitionAssignment);
        return definition;
    }

//    DEFINITION_ASSIGNMENT ::= PRIMITIVE_CONSTANT >> identifier ; DEFINITION_ASSIGNMENT
//    DEFINITION_ASSIGNMENT ::= ''
    private static ASTNode convertDefinitionAssignmentList(Node node) {
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
        definitionInstance.nextDefinition = convertDefinitionAssignmentList(definitionAssignment);
        return definitionInstance;
    }

//    FUNCTION ::= fun identifier ( PARAM_LIST ) : RETURN_TYPE { STATEMENT_LIST }
    private static ASTNode convertFunction(Node node) {
        Node identifier = node.firstChild.neighbor;
        Node paramList = identifier.neighbor.neighbor;
        Node returnType = paramList.neighbor.neighbor.neighbor;
        Node stmtList = returnType.neighbor.neighbor;

        ASTNodes.Function function = new ASTNodes.Function();
        function.name = ((ParseTree.TerminalNode)identifier).value;
        function.paramList = convertParamList(paramList);
        function.returnType = convertType(returnType);
        function.firstStatement = convertStatementList(stmtList);

        return function;
    }

//    PARAM_LIST ::= identifier : TYPE PARAM_LIST_TAIL
//    PARAM_LIST ::= ''
//
//    PARAM_LIST_TAIL ::= , identifier : TYPE PARAM_LIST_TAIL
//    PARAM_LIST_TAIL ::= ''
    private static ASTNode convertParamList(Node node) {
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
        functionParam.nextParam = convertParamList(third);
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
        statement.nextStatement = convertStatementList(second);

        first = (ParseTree.NonTerminalNode) first.firstChild;

        if (first.nodeType == NodeType.WHILE_STATEMENT) {
            statement.statement = convertWhileStatement(first);
        } else if (first.nodeType == NodeType.IF_STATEMENT) {
            statement.statement = convertIfStatement(first);
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
            statement.statement = convertFreeArrStatement(first);
        } else if (first.nodeType == NodeType.RETURN_STATEMENT) {
            statement.statement = convertReturnStatement(first);
        } else if (first.nodeType == NodeType.BREAK_STATEMENT) {
            statement.statement = convertBreakStatement(first);
        } else {
            statement.statement = convertContinueStatement(first);
        }
        return statement;
    }

//    DEFINE_VAR ::= let identifier : TYPE ;
    private static ASTNode convertDefineVar(Node node) {
        ParseTree.TerminalNode identifier = (ParseTree.TerminalNode) node.firstChild.neighbor;
        Node instantiateType = identifier.neighbor.neighbor;
        ASTNodes.DefineVar defineVar = new ASTNodes.DefineVar();
        defineVar.identifier = identifier.value;
        defineVar.type = convertType(instantiateType);

        return defineVar;
    }

//    ALLOC_ARR ::= alloc ALLOC_ARR_TYPE >> identifier ;
    private static ASTNode convertAllocArrStatement(Node node) {
        Node allocArrType = node.firstChild.neighbor;
        Node identifier = allocArrType.neighbor.neighbor;
        ASTNodes.AllocArr allocArr = new ASTNodes.AllocArr();
        allocArr.identifier = ((ParseTree.TerminalNode) identifier).value;
        allocArr.allocArrType = convertAllocArrType(allocArrType);

        return allocArr;
    }

//    FREE_ARR ::= free identifier ;
    private static ASTNode convertFreeArrStatement(Node node) {
        Node identifier = node.firstChild.neighbor;
        ASTNodes.FreeArr freeArr = new ASTNodes.FreeArr();
        freeArr.identifier = ((ParseTree.TerminalNode) identifier).value;

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

//    INPUT_STATEMENT ::= input identifier ARRAY_EXTENSION ;
    private static ASTNode convertInputStatement(Node node) {
        Node identifier = node.firstChild.neighbor;
        Node arrExt = identifier.neighbor;
        ASTNodes.Input input = new ASTNodes.Input();
        if (arrExt.firstChild == null) {
            ASTNodes.Identifier returnIdentifier = new ASTNodes.Identifier();
            returnIdentifier.value = ((ParseTree.TerminalNode) identifier).value;
            input.identifier = returnIdentifier;
        } else {
            // ARRAY_CALL
            ASTNodes.ArrayCall arrayCall = new ASTNodes.ArrayCall();
            arrayCall.identifier = ((ParseTree.TerminalNode) identifier).value;
            arrayCall.callIdx = convertArrayExtension(arrExt);
            input.identifier = arrayCall;
        }
        return input;
    }

//    OUTPUT_STATEMENT ::= output EXPRESSION ;
    private static ASTNode convertOutputStatement(Node node) {
        Node expr = node.firstChild.neighbor;
        ASTNodes.Output output = new ASTNodes.Output();
        output.node = convertExpression(expr);
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
//    CLOSER ::= eq identifier ARRAY_EXTENSION
    private static ASTNode convertCloser(Node node) {
        Node first = node.firstChild;
        if (((ParseTree.TerminalNode)first).tokenType == Token.TokenType.RETURN_KEYWORD) {
            return new ASTNodes.Return();
        } else {
            ParseTree.TerminalNode identifier = (ParseTree.TerminalNode)first.neighbor;
            Node arrExt = identifier.neighbor;
            ASTNodes.Eq eq = new ASTNodes.Eq();
            eq.identifier = identifier.value;
            eq.arrCall = convertArrayExtension(arrExt);
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
                    expression.value = ((ParseTree.TerminalNode) mid.firstChild).value;
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
                expression.value = ((ParseTree.TerminalNode) right.firstChild).value;
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
            not.value = "!";
            not.left = convertPrimary(mid);
            return not;
        } else {
            return convertExpression(mid);
        }

    }

//    INSTANCE ::= identifier INSTANCE_TAIL
//    INSTANCE_TAIL ::= ( EXPR_LIST )
//    INSTANCE_TAIL ::= ARRAY_EXTENSION
    private static ASTNode convertInstance(Node node) {
        Node identifier = node.firstChild;
        Node instanceTail = identifier.neighbor;
        Node tailChild = instanceTail.firstChild;
        if (tailChild.isTerminal) {
            // EXPR_LIST
            ASTNodes.FunctionCall functionCall =  new ASTNodes.FunctionCall();
            functionCall.identifier = ((ParseTree.TerminalNode) identifier).value;
            functionCall.firstChild = convertFunctionCall(tailChild.neighbor);
            return functionCall;
        } else {
            if (tailChild.firstChild == null) {
                ASTNodes.Identifier returnIdentifier = new ASTNodes.Identifier();
                returnIdentifier.value = ((ParseTree.TerminalNode)identifier).value;
                return returnIdentifier;
            } else {
                // ARRAY_CALL
                ASTNodes.ArrayCall arrayCall = new ASTNodes.ArrayCall();
                arrayCall.identifier = ((ParseTree.TerminalNode) identifier).value;
                arrayCall.callIdx = convertArrayExtension(tailChild);
                return arrayCall;
            }
        }
    }

//    EXPR_LIST ::= EXPRESSION EXPR_LIST_TAIL
//    EXPR_LIST ::= ''
    private static ASTNode convertFunctionCall(Node node) {
        Node first = node.firstChild;
        if (first == null) {
            return null;
        }
        ASTNodes.FunctionCallIdx functionCallIdx = new ASTNodes.FunctionCallIdx();
        functionCallIdx.expression = convertExpression(first);
        functionCallIdx.next = convertFunctionCallTail(first.neighbor);
        return functionCallIdx;
    }

//    EXPR_LIST_TAIL ::= , EXPRESSION EXPR_LIST_TAIL
//    EXPR_LIST_TAIL ::= ''
    private static ASTNode convertFunctionCallTail(Node node) {
        Node first = node.firstChild;
        if (first == null) {
            return null;
        }
        ASTNodes.FunctionCallIdx functionCallIdx = new ASTNodes.FunctionCallIdx();
        ASTNode convertedExpression = convertExpression(first.neighbor);
        ASTNode next = convertFunctionCallTail(first.neighbor.neighbor);
        functionCallIdx.expression = convertedExpression;
        functionCallIdx.next = next;
        return functionCallIdx;
    }

//    RETURN_TYPE ::= TYPE
//    RETURN_TYPE ::= void
//
//    TYPE ::= PRIMITIVE_TYPE ARRAY_EMPTY_EXTENSION
    private static ASTNode convertType(Node node) {
        Node first = node.firstChild;
        if (first.isTerminal) {
            return new ASTNodes.VoidType();
        }

        if (((ParseTree.NonTerminalNode) first).nodeType == NodeType.TYPE) {
            first = first.firstChild;
        }

        Node arrEmptyExt = first.neighbor.firstChild;
        boolean hasArrExt = arrEmptyExt != null;

        if (((ParseTree.TerminalNode)first.firstChild).tokenType == Token.TokenType.BOOL_KEYWORD) {
            ASTNodes.BoolType type = new ASTNodes.BoolType();
            type.arrExt = hasArrExt;
            return type;
        } else if (((ParseTree.TerminalNode)first.firstChild).tokenType == Token.TokenType.CHAR_KEYWORD) {
            ASTNodes.CharType type = new ASTNodes.CharType();
            type.arrExt = hasArrExt;
            return type;
        } else if (((ParseTree.TerminalNode)first.firstChild).tokenType == Token.TokenType.INT_KEYWORD) {
            ASTNodes.IntType type = new ASTNodes.IntType();
            type.arrExt = hasArrExt;
            return type;
        } else {
            ASTNodes.FloatType type = new ASTNodes.FloatType();
            type.arrExt = hasArrExt;
            return type;
        }
    }

//    INSTANTIATE_TYPE ::= PRIMITIVE_TYPE [ MOD_EXPR ]
    private static ASTNode convertAllocArrType(Node node) {
        Node first = node.firstChild;
        Node expr = first.neighbor.neighbor;

        ASTNodes.ArrayCallIdx callIdx = new ASTNodes.ArrayCallIdx();
        callIdx.expression = convertExpression(expr);

        if (((ParseTree.TerminalNode)first.firstChild).tokenType == Token.TokenType.BOOL_KEYWORD) {
            ASTNodes.BoolInitType type = new ASTNodes.BoolInitType();
            type.arrExt = callIdx;
            return type;
        } else if (((ParseTree.TerminalNode)first.firstChild).tokenType == Token.TokenType.CHAR_KEYWORD) {
            ASTNodes.CharInitType type = new ASTNodes.CharInitType();
            type.arrExt = callIdx;
            return type;
        } else if (((ParseTree.TerminalNode)first.firstChild).tokenType == Token.TokenType.INT_KEYWORD) {
            ASTNodes.IntInitType type = new ASTNodes.IntInitType();
            type.arrExt = callIdx;
            return type;
        } else {
            ASTNodes.FloatInitType type = new ASTNodes.FloatInitType();
            type.arrExt = callIdx;
            return type;
        }
    }

//    ARRAY_EXTENSION ::= [ MOD_EXPR ]
//    ARRAY_EXTENSION ::= ''
    private static ASTNode convertArrayExtension(Node node) {
        Node first = node.firstChild;
        if (first == null) {
            return null;
        }

        Node expr = first.neighbor;
        ASTNode convertedExpr = convertExpression(expr);
        ASTNodes.ArrayCallIdx arrayCallIdx = new ASTNodes.ArrayCallIdx();
        arrayCallIdx.expression = convertedExpr;
        return arrayCallIdx;
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
        if (operator.value.equals("*") || operator.value.equals("/")) {
            return ExprType.MUL;
        } else if (operator.value.equals("+") || operator.value.equals("-")) {
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
            output.node = transformAllExpressions(output.node);
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
