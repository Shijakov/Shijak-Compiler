package com.company.compiler.abstract_syntax_tree.infrastructure;

import com.company.compiler.abstract_syntax_tree.model.ASNode;
import com.company.compiler.abstract_syntax_tree.model.ASTree;
import com.company.compiler.abstract_syntax_tree.model.nodes.*;
import com.company.compiler.common.symbol.Terminal;
import com.company.compiler.common.token.RecognisedToken;
import com.company.compiler.parser.model.ParseNode;
import com.company.compiler.parser.model.ParseTree;

import static com.company.compiler.shijak.ShijakNonTerminals.*;
import static com.company.compiler.shijak.ShijakTokens.*;

public class ParseTreeToASTreeConverter {
    public static ASTree from(ParseTree tree) {
        return new ASTree(convertProgram(tree.getRoot().firstChild));
    }
    
//    PROGRAM ::= DEFINITION FUNCTION_OR_BAG
//    PROGRAM ::= FUNCTION_OR_BAG
    private static ASNode convertProgram(ParseNode node) {
        ParseNode first = node.firstChild;
        ParseNode second = first.neighbor;
        var program = new Program();
        program.line = first.line;

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
    private static ASNode convertDefinition(ParseNode node) {
        ParseNode definitionAssignment = node.firstChild.neighbor.neighbor;
        Definition definition = new Definition();
        definition.line = definitionAssignment.line;

        definition.firstDefinition = convertDefinitionAssignment(definitionAssignment);
        return definition;
    }

//    DEFINITION_ASSIGNMENT ::= PRIMITIVE_CONSTANT >> identifier ; DEFINITION_ASSIGNMENT
//    DEFINITION_ASSIGNMENT ::= ''
    private static ASNode convertDefinitionAssignment(ParseNode node) {
        ParseNode first = node.firstChild;
        if (first == null) {
            return null;
        }
        ParseNode identifier = first.neighbor.neighbor;
        ParseNode definitionAssignment = identifier.neighbor.neighbor;

        DefinitionInstance definitionInstance = new DefinitionInstance();
        definitionInstance.line = identifier.line;

        ASNode primitiveConstant = convertPrimitiveConstant(first);
        definitionInstance.identifier = getValue(identifier);
        definitionInstance.primitiveConstant = primitiveConstant;
        definitionInstance.nextDefinition = convertDefinitionAssignment(definitionAssignment);
        return definitionInstance;
    }

    //    FUNCTION_OR_BAG ::= FUNCTION FUNCTION_OR_BAG
//    FUNCTION_OR_BAG ::= BAG_DEFINITION FUNCTION_OR_BAG
//    FUNCTION_OR_BAG ::= ''
    private static ASNode convertFunctionOrBag(ParseNode node) {
        ParseNode first = node.firstChild;
        if (first == null) {
            return null;
        }

        ParseNode second = first.neighbor;
        var constructList = new ConstructList();
        constructList.line = first.line;

        if (first.getValue().equals(FUNCTION)) {
            constructList.construct = convertFunctionDefinition(first);
        } else {
            constructList.construct = convertBagDefinition(first);
        }
        constructList.nextConstruct = convertFunctionOrBag(second);
        return constructList;
    }

    //    BAG_DEFINITION ::= bag identifier { BAG_DEFINITION_PARAMETER_LIST }
    private static ASNode convertBagDefinition(ParseNode node) {
        var identifier = node.firstChild.neighbor;
        var bagDefinitionParameterList = identifier.neighbor.neighbor;

        var bagDefinition = new BagDefinition();
        bagDefinition.line = identifier.line;

        bagDefinition.bagName = getValue(identifier);
        bagDefinition.paramList = convertBagDefinitionParameterList(bagDefinitionParameterList);

        return bagDefinition;
    }

    //    BAG_DEFINITION_PARAMETER_LIST ::= identifier : TYPE BAG_DEFINITION_PARAMETER_LIST_TAIL
//    BAG_DEFINITION_PARAMETER_LIST ::= ''
//    BAG_DEFINITION_PARAMETER_LIST_TAIL ::= , identifier : TYPE BAG_DEFINITION_PARAMETER_LIST_TAIL
//    BAG_DEFINITION_PARAMETER_LIST_TAIL ::= ''
    private static ASNode convertBagDefinitionParameterList(ParseNode node) {
        var identifier = node.firstChild;

        if (identifier == null) {
            return null;
        }

        if (identifier.getValue().equals(commaToken)) {
            identifier = identifier.neighbor;
        }

        var type = identifier.neighbor.neighbor;
        var bagDefinitionParameterListTail = type.neighbor;

        var bagParameter = new BagParam();
        bagParameter.line = identifier.line;

        bagParameter.identifier = getValue(identifier);
        bagParameter.type = convertType(type);
        bagParameter.nextParam = convertBagDefinitionParameterList(bagDefinitionParameterListTail);

        return bagParameter;
    }

    //    FUNCTION ::= fun identifier ( PARAM_LIST ) : RETURN_TYPE { STATEMENT_LIST }
    private static ASNode convertFunctionDefinition(ParseNode node) {
        var identifier = node.firstChild.neighbor;
        var paramList = identifier.neighbor.neighbor;
        var returnType = paramList.neighbor.neighbor.neighbor;
        var statementList = returnType.neighbor.neighbor;

        var functionDefinition = new FunctionDefinition();
        functionDefinition.line = identifier.line;

        functionDefinition.functionName = getValue(identifier);
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
    private static ASNode convertFunctionParamList(ParseNode node) {
        ParseNode first = node.firstChild;
        if (first == null) {
            return null;
        }
        if (first.getValue().equals(commaToken)) {
            first = first.neighbor;
        }

        ParseNode second = first.neighbor.neighbor;
        ParseNode third = second.neighbor;

        FunctionParam functionParam = new FunctionParam();
        functionParam.line = first.line;

        functionParam.identifier = getValue(first);
        functionParam.type = convertType(second);
        functionParam.nextParam = convertFunctionParamList(third);
        return functionParam;
    }

    //    STATEMENT_LIST ::= STATEMENT STATEMENT_LIST
//    STATEMENT_LIST ::= ''
    private static ASNode convertStatementList(ParseNode node) {
        ParseNode first = node.firstChild;
        if (first == null) {
            return null;
        }
        ParseNode second = first.neighbor;

        Statement statement = new Statement();
        statement.line = first.line;

        first = first.firstChild;

        if (first.getValue().equals(WHILE_STATEMENT)) {
            statement.statement = convertWhileStatement(first);
        } else if (first.getValue().equals(IF_STATEMENT)) {
            statement.statement = convertIfStatement(first);
        } else if (first.getValue().equals(BREAK_STATEMENT)) {
            statement.statement = convertBreakStatement(first);
        } else if (first.getValue().equals(CONTINUE_STATEMENT)) {
            statement.statement = convertContinueStatement(first);
        } else if (first.getValue().equals(EXPRESSION_LIST)) {
            statement.statement = convertExpressionList(first);
        } else if (first.getValue().equals(DEFINE_VAR)) {
            statement.statement = convertDefineVar(first);
        } else if (first.getValue().equals(INPUT_STATEMENT)) {
            statement.statement = convertInputStatement(first);
        } else if (first.getValue().equals(OUTPUT_STATEMENT)){
            statement.statement = convertOutputStatement(first);
        } else if (first.getValue().equals(ALLOC_ARR)) {
            statement.statement = convertAllocArrStatement(first);
        } else if (first.getValue().equals(FREE_ARR)) {
            statement.statement = convertFreeStatement(first);
        } else if (first.getValue().equals(RETURN_STATEMENT)) {
            statement.statement = convertReturnStatement(first);
        } else if (first.getValue().equals(FILL_STATEMENT)) {
            statement.statement = convertFillStatement(first);
        }

        statement.nextStatement = convertStatementList(second);

        return statement;
    }

    //    DEFINE_VAR ::= let identifier : TYPE ;
    private static ASNode convertDefineVar(ParseNode node) {
        var identifier = node.firstChild.neighbor;
        var instantiateType = identifier.neighbor.neighbor;

        DefineVar defineVar = new DefineVar();
        defineVar.line = identifier.line;

        defineVar.identifier = getValue(identifier);
        defineVar.type = convertType(instantiateType);

        return defineVar;
    }

    //    FILL_STATEMENT ::= fill bag identifier >> ASSIGNABLE_INSTANCE ;
    private static ASNode convertFillStatement(ParseNode node) {
        var identifier = node.firstChild.neighbor.neighbor;
        var assignableInstance = identifier.neighbor.neighbor;

        var fillStatement = new FillBag();
        fillStatement.line = identifier.line;

        fillStatement.bagName = getValue(identifier);
        fillStatement.assignableInstance = convertAssignableInstance(assignableInstance);

        return fillStatement;
    }

    //    ALLOC_ARR ::= alloc PRIMITIVE_TYPE [ MOD_EXPR ] >> ASSIGNABLE_INSTANCE ;
    private static ASNode convertAllocArrStatement(ParseNode node) {
        var primitiveType = node.firstChild.neighbor;
        var expression = primitiveType.neighbor.neighbor;
        var assignableInstance = expression.neighbor.neighbor.neighbor;

        var allocArrStatement = new AllocArr();
        allocArrStatement.line = primitiveType.line;

        allocArrStatement.type = convertPrimitiveType(primitiveType);
        allocArrStatement.expression = convertExpression(expression);
        allocArrStatement.assignableInstance = convertAssignableInstance(assignableInstance);

        return allocArrStatement;
    }

    //    FREE_ARR ::= free ASSIGNABLE_INSTANCE ;
    private static ASNode convertFreeStatement(ParseNode node) {
        var assignableInstance = node.firstChild.neighbor;

        var freeArr = new FreeInstance();
        freeArr.line = assignableInstance.line;

        freeArr.assignableInstance = convertAssignableInstance(assignableInstance);

        return freeArr;
    }

    //    RETURN_STATEMENT ::= return ;
    private static ASNode convertReturnStatement(ParseNode node) {
        var returnStatement = new Return();
        returnStatement.line = node.line;

        return returnStatement;
    }

    //    BREAK_STATEMENT ::= break ;
    private static ASNode convertBreakStatement(ParseNode node) {
        var breakStatement = new Break();
        breakStatement.line = node.line;

        return breakStatement;
    }

    //    CONTINUE_STATEMENT ::= continue ;
    private static ASNode convertContinueStatement(ParseNode node) {
        var continueStatement = new Continue();
        continueStatement.line = node.line;

        return continueStatement;
    }

    //    INPUT_STATEMENT ::= input ASSIGNABLE_INSTANCE ;
    private static ASNode convertInputStatement(ParseNode node) {
        var assignableInstance = node.firstChild.neighbor;

        var input = new Input();
        input.line = assignableInstance.line;

        input.assignableInstance = convertAssignableInstance(assignableInstance);

        return input;
    }

    //    OUTPUT_STATEMENT ::= output EXPRESSION ;
    private static ASNode convertOutputStatement(ParseNode node) {
        ParseNode expr = node.firstChild.neighbor;
        Output output = new Output();
        output.line = expr.line;

        output.expression = convertExpression(expr);
        return output;
    }

//    WHILE_STATEMENT ::= while ( EXPRESSION ) { STATEMENT_LIST }
    private static ASNode convertWhileStatement(ParseNode node) {
        ParseNode condition = node.firstChild.neighbor.neighbor;
        ParseNode stmtList = condition.neighbor.neighbor.neighbor;

        While whileStatement = new While();
        whileStatement.line = condition.line;

        whileStatement.condition = convertExpression(condition);
        whileStatement.firstStatement = convertStatementList(stmtList);

        return whileStatement;
    }

//    IF_STATEMENT ::= if ( EXPRESSION ) { STATEMENT_LIST } ELIF_STATEMENT ELSE_STATEMENT
//    ELIF_STATEMENT ::= elif ( EXPRESSION ) { STATEMENT_LIST } ELIF_STATEMENT
//    ELIF_STATEMENT ::= ''
//    ELSE_STATEMENT ::= else { STATEMENT_LIST }
//    ELSE_STATEMENT ::= ''
    private static ASNode convertIfStatement(ParseNode node) {
        if (node.getValue().equals(IF_STATEMENT)) {
            If ifStatement = new If();
            ifStatement.line = node.line;

            ParseNode expr = node.firstChild.neighbor.neighbor;
            ParseNode stmtList = expr.neighbor.neighbor.neighbor;
            ParseNode elif = stmtList.neighbor.neighbor;
            ParseNode els = elif.neighbor;

            ifStatement.condition = convertExpression(expr);
            ifStatement.firstStatement = convertStatementList(stmtList);
            ifStatement.elif = convertIfStatement(elif);

            Elif currentElif = (Elif) ifStatement.elif;

            if (currentElif == null) {
                ifStatement.elif = convertIfStatement(els);
                return ifStatement;
            }
            while (currentElif.elif != null) {
                currentElif = (Elif) currentElif.elif;
            }
            currentElif.elif = convertIfStatement(els);

            return ifStatement;
        } else if (node.getValue().equals(ELIF_STATEMENT)) {
            if (node.firstChild == null) {
                return null;
            }
            Elif elifStatement = new Elif();
            elifStatement.line = node.line;

            ParseNode expr = node.firstChild.neighbor.neighbor;
            ParseNode stmtList = expr.neighbor.neighbor.neighbor;
            ParseNode elif = stmtList.neighbor.neighbor;

            elifStatement.condition = convertExpression(expr);
            elifStatement.firstStatement = convertStatementList(stmtList);
            elifStatement.elif = convertIfStatement(elif);

            return elifStatement;
        } else {
            if (node.firstChild == null) {
                return null;
            }
            Else elseStatement = new Else();
            elseStatement.line = node.line;

            ParseNode stmtList = node.firstChild.neighbor.neighbor;
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
    private static ASNode convertExpressionList(ParseNode node) {
        if (node.getValue().equals(EXPRESSION_LIST)) {
            ASNode expr = convertExpression(node.firstChild);
            ASNode nextExprList = convertExpressionList(node.firstChild.neighbor);
            ExpressionList expressionList = new ExpressionList();
            expressionList.line = node.line;

            expressionList.nextExprList = nextExprList;
            expressionList.exprOrCloser = expr;
            return expressionList;
        } else if (node.getValue().equals(EXPRESSION_LIST_TAIL)) {
            if (node.firstChild == null) {
                return null;
            } else {
                return convertExpressionList(node.firstChild.neighbor);
            }
        } else {
            ParseNode first = node.firstChild;
            ExpressionList expressionList = new ExpressionList();
            expressionList.line = first.line;

            if (first.getValue().equals(CLOSER)) {
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
    private static ASNode convertCloser(ParseNode node) {
        ParseNode first = node.firstChild;
        if (first.getValue().equals(returnToken)) {
            var returnStatement = new Return();
            returnStatement.line = first.line;

            return returnStatement;
        } else {
            var assignableInstance = first.neighbor;

            var eq = new Eq();
            eq.line = assignableInstance.line;

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
    private static ASNode convertExpression(ParseNode node) {
        ParseNode left = node.firstChild;
        if (left == null) {
            return null;
        }
        ParseNode mid = left.neighbor;
        ParseNode right = mid.neighbor;

        if (right == null) {
            if (node.getValue().equals(EXPRESSION_TAIL)) {
                return convertExpression(mid);
            } else {
                ASNode convertedMidExpression = convertExpression(mid);
                if (convertedMidExpression == null) {
                    if (left.getValue().equals(PRIMARY)) {
                        return convertPrimary(left);
                    } else {
                        return convertExpression(left);
                    }
                } else {
                    BinaryOperator expression = new BinaryOperator();
                    expression.line = convertedMidExpression.line;

                    expression.operator = getValue(mid.firstChild);
                    expression.right = convertedMidExpression;
                    if (left.getValue().equals(PRIMARY)) {
                        expression.left = convertPrimary(left);
                    } else {
                        expression.left = convertExpression(left);
                    }
                    return expression;
                }
            }
        } else {
            ASNode convertedRightExpression = convertExpression(right);
            if (convertedRightExpression == null) {
                if (mid.getValue().equals(PRIMARY)) {
                    return convertPrimary(mid);
                } else {
                    return convertExpression(mid);
                }
            } else {
                convertedRightExpression.line = right.line;
                BinaryOperator expression = new BinaryOperator();
                expression.line = convertedRightExpression.line;

                expression.operator = getValue(right.firstChild);
                expression.right = convertedRightExpression;
                if (mid.getValue().equals(PRIMARY)) {
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
    private static ASNode convertPrimary(ParseNode node) {
        ParseNode left = node.firstChild;
        ParseNode mid = left.neighbor;

        if (mid == null) {
            if (left.getValue() instanceof Terminal) {
                var inKeyword = new InKeyword();
                inKeyword.line = left.line;
                return inKeyword;
            } else {
                if (left.getValue().equals(PRIMITIVE_CONSTANT)) {
                    return convertPrimitiveConstant(left);
                } else {
                    return convertInstance(left);
                }
            }
        }

        ParseNode right = mid.neighbor;

        if (right == null) {
            UnaryOperator not = new UnaryOperator();
            not.line = mid.line;

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
    private static ASNode convertInstance(ParseNode node) {
        ParseNode identifier = node.firstChild;
        ParseNode instanceTail = identifier.neighbor;
        ParseNode tailChild = instanceTail.firstChild;
        if (tailChild.getValue() instanceof Terminal) {
            // EXPR_LIST
            FunctionCall functionCall =  new FunctionCall();
            functionCall.line = identifier.line;

            functionCall.identifier = getValue(identifier);
            functionCall.firstArgument = convertFunctionCall(tailChild.neighbor);
            return functionCall;
        } else {
            var variable = new Variable();
            variable.line = identifier.line;

            variable.name = getValue(identifier);
            variable.callExtension = convertAssignableInstanceTail(tailChild);

            return variable;
        }
    }

    //    ASSIGNABLE_INSTANCE ::= identifier ASSIGNABLE_INSTANCE_TAIL
    private static ASNode convertAssignableInstance(ParseNode node) {
        var identifier = node.firstChild;
        var assignableInstanceTail = identifier.neighbor;

        var assignableInstance = new Variable();
        assignableInstance.line = identifier.line;

        assignableInstance.name = getValue(identifier);
        assignableInstance.callExtension = convertAssignableInstanceTail(assignableInstanceTail);

        return assignableInstance;
    }

    //    ASSIGNABLE_INSTANCE_TAIL ::= [ MOD_EXPR ] DOT_TAIL
//    ASSIGNABLE_INSTANCE_TAIL ::= DOT_TAIL
    private static ASNode convertAssignableInstanceTail(ParseNode node) {
        var first = node.firstChild;

        if (first.getValue() instanceof Terminal) {
            var expression = first.neighbor;
            var dotTail = expression.neighbor.neighbor;

            var arrayCallExtension = new ArrayCallExtension();
            arrayCallExtension.line = expression.line;

            arrayCallExtension.expression = convertExpression(expression);
            arrayCallExtension.callExtension = convertDotTail(dotTail);

            return arrayCallExtension;
        } else {
            return convertDotTail(first);
        }
    }

    //    DOT_TAIL ::= . identifier ASSIGNABLE_INSTANCE_TAIL
//    DOT_TAIL ::= ''
    private static ASNode convertDotTail(ParseNode node) {
        var first = node.firstChild;

        if (first == null) {
            return null;
        }

        var identifier = first.neighbor;
        var assignableInstanceTail = identifier.neighbor;

        var bagCallExtension = new BagCallExtension();
        bagCallExtension.line = identifier.line;

        bagCallExtension.fieldName = getValue(identifier);
        bagCallExtension.callExtension = convertAssignableInstanceTail(assignableInstanceTail);

        return bagCallExtension;
    }

    //    EXPR_LIST ::= EXPRESSION EXPR_LIST_TAIL
//    EXPR_LIST ::= ''
    private static ASNode convertFunctionCall(ParseNode node) {
        ParseNode first = node.firstChild;
        if (first == null) {
            return null;
        }
        var argument = new FunctionCallArgument();
        argument.line = first.line;

        argument.expression = convertExpression(first);
        argument.next = convertFunctionCallTail(first.neighbor);
        return argument;
    }

    //    EXPR_LIST_TAIL ::= , EXPRESSION EXPR_LIST_TAIL
//    EXPR_LIST_TAIL ::= ''
    private static ASNode convertFunctionCallTail(ParseNode node) {
        ParseNode first = node.firstChild;
        if (first == null) {
            return null;
        }

        var argument = new FunctionCallArgument();
        argument.line = first.neighbor.line;

        argument.expression = convertExpression(first.neighbor);
        argument.next = convertFunctionCallTail(first.neighbor.neighbor);

        return argument;
    }

    //    RETURN_TYPE ::= TYPE
//    RETURN_TYPE ::= void
//
//    TYPE ::= PRIMITIVE_TYPE ARRAY_EMPTY_EXTENSION
    private static ASNode convertType(ParseNode node) {
        var typeNode = node;
        if (node.getValue().equals(RETURN_TYPE)) {
            if (node.firstChild.getValue() instanceof Terminal) {
                // RETURN_TYPE ::= void
                var voidType = new VoidType();
                voidType.line = node.firstChild.line;

                return new VoidType();
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

        var type = (Type) convertPrimitiveType(primitiveType);

        type.hasArrayExtension = hasArrExt;
        return type;
    }

    //    PRIMITIVE_TYPE ::= int
//    PRIMITIVE_TYPE ::= float
//    PRIMITIVE_TYPE ::= char
//    PRIMITIVE_TYPE ::= bool
//    PRIMITIVE_TYPE ::= bag identifier
    private static ASNode convertPrimitiveType(ParseNode node) {
        var primitiveTypeName = node.firstChild;
        Type returnType = null;
        if (primitiveTypeName.getValue().equals(boolToken)) {
            // PRIMITIVE_TYPE ::= bool
            returnType = new BoolType();
        } else if (primitiveTypeName.getValue().equals(charToken)) {
            // PRIMITIVE_TYPE ::= char
            returnType = new CharType();
        } else if (primitiveTypeName.getValue().equals(intToken)) {
            // PRIMITIVE_TYPE ::= int
            returnType = new IntType();
        } else if (primitiveTypeName.getValue().equals(floatToken)) {
            // PRIMITIVE_TYPE ::= float
            returnType = new FloatType();
        } else {
            // PRIMITIVE_TYPE ::= bag identifier
            var primitiveType = new BagType();
            primitiveType.bagName = getValue(primitiveTypeName.neighbor);

            returnType = primitiveType;
        }

        returnType.line = primitiveTypeName.line;
        return returnType;
    }

    //    PRIMITIVE_CONSTANT ::= char_constant
//    PRIMITIVE_CONSTANT ::= bool_constant
//    PRIMITIVE_CONSTANT ::= int_constant
//    PRIMITIVE_CONSTANT ::= float_constant
    private static ASNode convertPrimitiveConstant(ParseNode node) {
        ParseNode terminalNode = node.firstChild;
        Constant returnConstant = null;

        if (terminalNode.getValue().equals(charConstToken)) {
            returnConstant = new CharConstant();
        } else if (terminalNode.getValue().equals(trueConstToken) || terminalNode.getValue().equals(falseConstToken)) {
            returnConstant = new BoolConstant();
        } else if (terminalNode.getValue().equals(intConstToken)) {
            returnConstant = new IntConstant();
        } else {
            returnConstant = new FloatConstant();
        }
        returnConstant.value = getValue(terminalNode);
        returnConstant.line = terminalNode.line;
        return returnConstant;
    }

    private static String getValue(ParseNode node) {
        return ((RecognisedToken) node.getValue()).getValue();
    }
}
