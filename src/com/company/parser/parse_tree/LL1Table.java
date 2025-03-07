package com.company.parser.parse_tree;
import com.company.lexer.Token.TokenType;
import com.company.parser.parse_tree.ParseTree.Node;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LL1Table {
    public static class Pair {
        NodeType node;
        TokenType token;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return node == pair.node && token == pair.token;
        }

        @Override
        public int hashCode() {
            return Objects.hash(node, token);
        }

        public Pair(NodeType node, TokenType token) {
            this.node = node;
            this.token = token;
        }
    }

    Map< Pair, Consumer< Stack<Node> > > table;
    StringToNodeMapper stringToNodeMapper;

    public LL1Table() {
        table = new HashMap<>();
        stringToNodeMapper = new StringToNodeMapper();

        fillForS();
        fillForProgram();
        fillForDefinition();
        fillForDefinitionAssignment();
        fillForFunction();
        fillForParamList();
        fillForParamListTail();
        fillForStatementList();
        fillForStatement();
        fillForDefineVar();
        fillForAllocArr();
        fillForFreeArr();
        fillForReturnStatement();
        fillForInputStatement();
        fillForOutputStatement();
        fillForWhileStatement();
        fillForBreakStatement();
        fillForContinueStatement();
        fillForIf();
        fillForElif();
        fillForElse();
        fillForExpressionList();
        fillForExpressionListTail();
        fillForExpressionOrCloser();
        fillForCloser();
        fillForExpression();
        fillForExpressionTail();
        fillForModExpr();
        fillForModExprTail();
        fillForAddExpr();
        fillForAddExprTail();
        fillForMulExpr();
        fillForMulExprTail();
        fillForPrimary();
        fillForInstance();
        fillForInstanceTail();
        fillForExprList();
        fillForExprListTail();
        fillForReturnType();
        fillForType();
        fillForArrayEmptyExtension();
        fillForPrimitiveType();
        fillForPrimitiveConstant();
        // NEW VERSION
        fillForFunctionOrBag();
        fillForBagDefinition();
        fillForBagDefinitionParameterList();
        fillForBagDefinitionParameterListTail();
        fillForFillStatement();
        fillForAssignableInstance();
        fillForAssignableInstanceTail();
        fillForDotTail();
    }

    private void putInTable(NodeType nodeType, TokenType tokenType, String nodeString) {
        table.put(new Pair(nodeType, tokenType), (stack) -> updateStackAndTree(stack, nodeString));
    }

    private List<Node> getNodesFromString(String nodeString) {
        List<String> stringNodes = Arrays.asList(nodeString.split(" "));
        return stringNodes.stream().map(node -> this.stringToNodeMapper.getNode(node)).collect(Collectors.toList());
    }

    private void pushToStack(List<Node> nodes, Stack<Node> stack) {
        for (int idx = nodes.size() - 1 ; idx >= 0 ; idx--) {
            stack.push(nodes.get(idx));
        }
    }

    private void updateStackAndTree(Stack<Node> stack, String nodeString) {
        Node firstNode = stack.pop();
        if (nodeString.isEmpty()) {
            return;
        }
        List<Node> nodes = getNodesFromString(nodeString);
        firstNode.attachNodes(nodes);
        pushToStack(nodes, stack);
    }

    private void fillForS() {
        putInTable(NodeType.S, TokenType.TERMINAL, "PROGRAM $");
        putInTable(NodeType.S, TokenType.DEFINE_KEYWORD, "PROGRAM $");
        putInTable(NodeType.S, TokenType.BAG_KEYWORD, "PROGRAM $");
        putInTable(NodeType.S, TokenType.FUNCTION_KEYWORD, "PROGRAM $");
    }

    private void fillForProgram() {
        putInTable(NodeType.PROGRAM, TokenType.TERMINAL, "FUNCTION_OR_BAG");
        putInTable(NodeType.PROGRAM, TokenType.DEFINE_KEYWORD, "DEFINITION FUNCTION_OR_BAG");
        putInTable(NodeType.PROGRAM, TokenType.BAG_KEYWORD, "FUNCTION_OR_BAG");
        putInTable(NodeType.PROGRAM, TokenType.FUNCTION_KEYWORD, "FUNCTION_OR_BAG");
    }

    private void fillForDefinition() {
        putInTable(NodeType.DEFINITION, TokenType.DEFINE_KEYWORD, "define { DEFINITION_ASSIGNMENT }");
    }

    private void fillForDefinitionAssignment() {
        putInTable(NodeType.DEFINITION_ASSIGNMENT, TokenType.CLOSE_CURLY_BRACKET, "");
        putInTable(NodeType.DEFINITION_ASSIGNMENT, TokenType.CHAR_CONSTANT, "PRIMITIVE_CONSTANT >> identifier ; DEFINITION_ASSIGNMENT");
        putInTable(NodeType.DEFINITION_ASSIGNMENT, TokenType.BOOL_CONSTANT, "PRIMITIVE_CONSTANT >> identifier ; DEFINITION_ASSIGNMENT");
        putInTable(NodeType.DEFINITION_ASSIGNMENT, TokenType.INT_CONSTANT, "PRIMITIVE_CONSTANT >> identifier ; DEFINITION_ASSIGNMENT");
        putInTable(NodeType.DEFINITION_ASSIGNMENT, TokenType.FLOAT_CONSTANT, "PRIMITIVE_CONSTANT >> identifier ; DEFINITION_ASSIGNMENT");
    }

    private void fillForFunctionOrBag() {
        putInTable(NodeType.FUNCTION_OR_BAG, TokenType.TERMINAL, "");
        putInTable(NodeType.FUNCTION_OR_BAG, TokenType.BAG_KEYWORD, "BAG_DEFINITION FUNCTION_OR_BAG");
        putInTable(NodeType.FUNCTION_OR_BAG, TokenType.FUNCTION_KEYWORD, "FUNCTION FUNCTION_OR_BAG");
    }

    private  void fillForBagDefinition() {
        putInTable(NodeType.BAG_DEFINITION, TokenType.BAG_KEYWORD, "bag identifier { BAG_DEFINITION_PARAMETER_LIST }");
    }

    private void fillForBagDefinitionParameterList() {
        putInTable(NodeType.BAG_DEFINITION_PARAMETER_LIST, TokenType.CLOSE_CURLY_BRACKET, "");
        putInTable(NodeType.BAG_DEFINITION_PARAMETER_LIST, TokenType.IDENTIFIER, "identifier : TYPE BAG_DEFINITION_PARAMETER_LIST_TAIL");
    }

    private void fillForBagDefinitionParameterListTail() {
        putInTable(NodeType.BAG_DEFINITION_PARAMETER_LIST_TAIL, TokenType.CLOSE_CURLY_BRACKET, "");
        putInTable(NodeType.BAG_DEFINITION_PARAMETER_LIST_TAIL, TokenType.COMMA, ", identifier : TYPE BAG_DEFINITION_PARAMETER_LIST_TAIL");
    }

    private void fillForFunction() {
        putInTable(NodeType.FUNCTION, TokenType.FUNCTION_KEYWORD, "fun identifier ( PARAM_LIST ) : RETURN_TYPE { STATEMENT_LIST }");
    }

    private void fillForParamList() {
        putInTable(NodeType.PARAM_LIST, TokenType.IDENTIFIER, "identifier : TYPE PARAM_LIST_TAIL");
        putInTable(NodeType.PARAM_LIST, TokenType.CLOSE_BRACKET, "");
    }

    private void fillForParamListTail() {
        putInTable(NodeType.PARAM_LIST_TAIL, TokenType.COMMA, ", identifier : TYPE PARAM_LIST_TAIL");
        putInTable(NodeType.PARAM_LIST_TAIL, TokenType.CLOSE_BRACKET, "");
    }

    private void fillForStatementList() {
        putInTable(NodeType.STATEMENT_LIST, TokenType.CLOSE_CURLY_BRACKET, "");
        putInTable(NodeType.STATEMENT_LIST, TokenType.IDENTIFIER, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.OPEN_BRACKET, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.FILL_KEYWORD, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.RETURN_KEYWORD, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.BREAK_KEYWORD, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.CONTINUE_KEYWORD, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.LET_KEYWORD, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.ALLOC_KEYWORD, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.FREE_KEYWORD, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.INPUT_KEYWORD, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.OUTPUT_KEYWORD, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.WHILE_KEYWORD, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.IF_KEYWORD, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.NOT_OPERATOR, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.IN_KEYWORD, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.CHAR_CONSTANT, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.BOOL_CONSTANT, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.INT_CONSTANT, "STATEMENT STATEMENT_LIST");
        putInTable(NodeType.STATEMENT_LIST, TokenType.FLOAT_CONSTANT, "STATEMENT STATEMENT_LIST");
    }

    public void fillForStatement() {
        putInTable(NodeType.STATEMENT, TokenType.IDENTIFIER, "EXPRESSION_LIST");
        putInTable(NodeType.STATEMENT, TokenType.OPEN_BRACKET, "EXPRESSION_LIST");
        putInTable(NodeType.STATEMENT, TokenType.FILL_KEYWORD, "FILL_STATEMENT");
        putInTable(NodeType.STATEMENT, TokenType.LET_KEYWORD, "DEFINE_VAR");
        putInTable(NodeType.STATEMENT, TokenType.ALLOC_KEYWORD, "ALLOC_ARR");
        putInTable(NodeType.STATEMENT, TokenType.FREE_KEYWORD, "FREE_ARR");
        putInTable(NodeType.STATEMENT, TokenType.RETURN_KEYWORD, "RETURN_STATEMENT");
        putInTable(NodeType.STATEMENT, TokenType.BREAK_KEYWORD, "BREAK_STATEMENT");
        putInTable(NodeType.STATEMENT, TokenType.CONTINUE_KEYWORD, "CONTINUE_STATEMENT");
        putInTable(NodeType.STATEMENT, TokenType.INPUT_KEYWORD, "INPUT_STATEMENT");
        putInTable(NodeType.STATEMENT, TokenType.OUTPUT_KEYWORD, "OUTPUT_STATEMENT");
        putInTable(NodeType.STATEMENT, TokenType.WHILE_KEYWORD, "WHILE_STATEMENT");
        putInTable(NodeType.STATEMENT, TokenType.IF_KEYWORD, "IF_STATEMENT");
        putInTable(NodeType.STATEMENT, TokenType.NOT_OPERATOR, "EXPRESSION_LIST");
        putInTable(NodeType.STATEMENT, TokenType.IN_KEYWORD, "EXPRESSION_LIST");
        putInTable(NodeType.STATEMENT, TokenType.CHAR_CONSTANT, "EXPRESSION_LIST");
        putInTable(NodeType.STATEMENT, TokenType.BOOL_CONSTANT, "EXPRESSION_LIST");
        putInTable(NodeType.STATEMENT, TokenType.INT_CONSTANT, "EXPRESSION_LIST");
        putInTable(NodeType.STATEMENT, TokenType.FLOAT_CONSTANT, "EXPRESSION_LIST");
    }

    private void fillForFillStatement() {
        putInTable(NodeType.FILL_STATEMENT, TokenType.FILL_KEYWORD, "fill bag identifier >> ASSIGNABLE_INSTANCE ;");
    }

    public void fillForDefineVar() {
        putInTable(NodeType.DEFINE_VAR, TokenType.LET_KEYWORD, "let identifier : TYPE ;");
    }

    public void fillForAllocArr() {
        putInTable(NodeType.ALLOC_ARR, TokenType.ALLOC_KEYWORD, "alloc PRIMITIVE_TYPE [ MOD_EXPR ] >> ASSIGNABLE_INSTANCE ;");
    }

    public void fillForFreeArr() {
        putInTable(NodeType.FREE_ARR, TokenType.FREE_KEYWORD, "free ASSIGNABLE_INSTANCE ;");
    }

    public void fillForReturnStatement() {
        putInTable(NodeType.RETURN_STATEMENT, TokenType.RETURN_KEYWORD, "return ;");
    }

    public void fillForBreakStatement() {
        putInTable(NodeType.BREAK_STATEMENT, TokenType.BREAK_KEYWORD, "break ;");
    }

    public void fillForContinueStatement() {
        putInTable(NodeType.CONTINUE_STATEMENT, TokenType.CONTINUE_KEYWORD, "continue ;");
    }

    public void fillForInputStatement() {
        putInTable(NodeType.INPUT_STATEMENT, TokenType.INPUT_KEYWORD, "input ASSIGNABLE_INSTANCE ;");
    }

    public void fillForOutputStatement() {
        putInTable(NodeType.OUTPUT_STATEMENT, TokenType.OUTPUT_KEYWORD, "output EXPRESSION ;");
    }

    public void fillForWhileStatement() {
        putInTable(NodeType.WHILE_STATEMENT, TokenType.WHILE_KEYWORD, "while ( EXPRESSION ) { STATEMENT_LIST }");
    }

    public void fillForIf() {
        putInTable(NodeType.IF_STATEMENT, TokenType.IF_KEYWORD, "if ( EXPRESSION ) { STATEMENT_LIST } ELIF_STATEMENT ELSE_STATEMENT");
    }

    public void fillForElif() {
        putInTable(NodeType.ELIF_STATEMENT, TokenType.CLOSE_CURLY_BRACKET, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.IDENTIFIER, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.OPEN_BRACKET, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.FILL_KEYWORD, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.RETURN_KEYWORD, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.BREAK_KEYWORD, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.CONTINUE_KEYWORD, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.LET_KEYWORD, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.ALLOC_KEYWORD, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.FREE_KEYWORD, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.INPUT_KEYWORD, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.OUTPUT_KEYWORD, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.WHILE_KEYWORD, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.IF_KEYWORD, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.ELIF_KEYWORD, "elif ( EXPRESSION ) { STATEMENT_LIST } ELIF_STATEMENT");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.ELSE_KEYWORD, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.NOT_OPERATOR, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.IN_KEYWORD, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.CHAR_CONSTANT, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.BOOL_CONSTANT, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.INT_CONSTANT, "");
        putInTable(NodeType.ELIF_STATEMENT, TokenType.FLOAT_CONSTANT, "");
    }

    public void fillForElse() {
        putInTable(NodeType.ELSE_STATEMENT, TokenType.CLOSE_CURLY_BRACKET, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.IDENTIFIER, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.OPEN_BRACKET, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.FILL_KEYWORD, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.RETURN_KEYWORD, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.BREAK_KEYWORD, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.CONTINUE_KEYWORD, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.LET_KEYWORD, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.ALLOC_KEYWORD, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.FREE_KEYWORD, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.INPUT_KEYWORD, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.OUTPUT_KEYWORD, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.WHILE_KEYWORD, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.IF_KEYWORD, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.ELSE_KEYWORD, "else { STATEMENT_LIST }");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.NOT_OPERATOR, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.IN_KEYWORD, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.CHAR_CONSTANT, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.BOOL_CONSTANT, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.INT_CONSTANT, "");
        putInTable(NodeType.ELSE_STATEMENT, TokenType.FLOAT_CONSTANT, "");
    }

    public void fillForExpressionList() {
        putInTable(NodeType.EXPRESSION_LIST, TokenType.IDENTIFIER, "EXPRESSION EXPRESSION_LIST_TAIL ;");
        putInTable(NodeType.EXPRESSION_LIST, TokenType.OPEN_BRACKET, "EXPRESSION EXPRESSION_LIST_TAIL ;");
        putInTable(NodeType.EXPRESSION_LIST, TokenType.NOT_OPERATOR, "EXPRESSION EXPRESSION_LIST_TAIL ;");
        putInTable(NodeType.EXPRESSION_LIST, TokenType.IN_KEYWORD, "EXPRESSION EXPRESSION_LIST_TAIL ;");
        putInTable(NodeType.EXPRESSION_LIST, TokenType.CHAR_CONSTANT, "EXPRESSION EXPRESSION_LIST_TAIL ;");
        putInTable(NodeType.EXPRESSION_LIST, TokenType.BOOL_CONSTANT, "EXPRESSION EXPRESSION_LIST_TAIL ;");
        putInTable(NodeType.EXPRESSION_LIST, TokenType.INT_CONSTANT, "EXPRESSION EXPRESSION_LIST_TAIL ;");
        putInTable(NodeType.EXPRESSION_LIST, TokenType.FLOAT_CONSTANT, "EXPRESSION EXPRESSION_LIST_TAIL ;");
    }

    public void fillForExpressionListTail() {
        putInTable(NodeType.EXPRESSION_LIST_TAIL, TokenType.EXPRESSION_OPERATOR, ">> EXPRESSION_OR_CLOSER");
        putInTable(NodeType.EXPRESSION_LIST_TAIL, TokenType.SEMI_COLON, "");
    }

    public void fillForExpressionOrCloser() {
        putInTable(NodeType.EXPRESSION_OR_CLOSER, TokenType.IDENTIFIER, "EXPRESSION EXPRESSION_LIST_TAIL");
        putInTable(NodeType.EXPRESSION_OR_CLOSER, TokenType.OPEN_BRACKET, "EXPRESSION EXPRESSION_LIST_TAIL");
        putInTable(NodeType.EXPRESSION_OR_CLOSER, TokenType.RETURN_KEYWORD, "CLOSER");
        putInTable(NodeType.EXPRESSION_OR_CLOSER, TokenType.EQ_KEYWORD, "CLOSER");
        putInTable(NodeType.EXPRESSION_OR_CLOSER, TokenType.NOT_OPERATOR, "EXPRESSION EXPRESSION_LIST_TAIL");
        putInTable(NodeType.EXPRESSION_OR_CLOSER, TokenType.IN_KEYWORD, "EXPRESSION EXPRESSION_LIST_TAIL");
        putInTable(NodeType.EXPRESSION_OR_CLOSER, TokenType.CHAR_CONSTANT, "EXPRESSION EXPRESSION_LIST_TAIL");
        putInTable(NodeType.EXPRESSION_OR_CLOSER, TokenType.BOOL_CONSTANT, "EXPRESSION EXPRESSION_LIST_TAIL");
        putInTable(NodeType.EXPRESSION_OR_CLOSER, TokenType.INT_CONSTANT, "EXPRESSION EXPRESSION_LIST_TAIL");
        putInTable(NodeType.EXPRESSION_OR_CLOSER, TokenType.FLOAT_CONSTANT, "EXPRESSION EXPRESSION_LIST_TAIL");
    }

    public void fillForCloser() {
        putInTable(NodeType.CLOSER, TokenType.RETURN_KEYWORD, "return");
        putInTable(NodeType.CLOSER, TokenType.EQ_KEYWORD, "eq ASSIGNABLE_INSTANCE");
    }

    public void fillForExpression() {
        putInTable(NodeType.EXPRESSION, TokenType.IDENTIFIER, "MOD_EXPR EXPRESSION_TAIL");
        putInTable(NodeType.EXPRESSION, TokenType.OPEN_BRACKET, "MOD_EXPR EXPRESSION_TAIL");
        putInTable(NodeType.EXPRESSION, TokenType.NOT_OPERATOR, "MOD_EXPR EXPRESSION_TAIL");
        putInTable(NodeType.EXPRESSION, TokenType.IN_KEYWORD, "MOD_EXPR EXPRESSION_TAIL");
        putInTable(NodeType.EXPRESSION, TokenType.CHAR_CONSTANT, "MOD_EXPR EXPRESSION_TAIL");
        putInTable(NodeType.EXPRESSION, TokenType.BOOL_CONSTANT, "MOD_EXPR EXPRESSION_TAIL");
        putInTable(NodeType.EXPRESSION, TokenType.INT_CONSTANT, "MOD_EXPR EXPRESSION_TAIL");
        putInTable(NodeType.EXPRESSION, TokenType.FLOAT_CONSTANT, "MOD_EXPR EXPRESSION_TAIL");
    }

    public void fillForExpressionTail() {
        putInTable(NodeType.EXPRESSION_TAIL, TokenType.CLOSE_CURLY_BRACKET, "");
        putInTable(NodeType.EXPRESSION_TAIL, TokenType.EXPRESSION_OPERATOR, "");
        putInTable(NodeType.EXPRESSION_TAIL, TokenType.SEMI_COLON, "");
        putInTable(NodeType.EXPRESSION_TAIL, TokenType.CLOSE_BRACKET, "");
        putInTable(NodeType.EXPRESSION_TAIL, TokenType.COMMA, "");
        putInTable(NodeType.EXPRESSION_TAIL, TokenType.GREATER_EQUAL_OPERATOR, ">= MOD_EXPR");
        putInTable(NodeType.EXPRESSION_TAIL, TokenType.LESS_EQUAL_OPERATOR, "<= MOD_EXPR");
        putInTable(NodeType.EXPRESSION_TAIL, TokenType.LESS_OPERATOR, "< MOD_EXPR");
        putInTable(NodeType.EXPRESSION_TAIL, TokenType.GREATER_OPERATOR, "> MOD_EXPR");
        putInTable(NodeType.EXPRESSION_TAIL, TokenType.EQUAL_OPERATOR, "== MOD_EXPR");
        putInTable(NodeType.EXPRESSION_TAIL, TokenType.NOT_EQUAL_OPERATOR, "!= MOD_EXPR");
    }

    public void fillForModExpr() {
        putInTable(NodeType.MOD_EXPR, TokenType.IDENTIFIER, "ADD_EXPR MOD_EXPR_TAIL");
        putInTable(NodeType.MOD_EXPR, TokenType.OPEN_BRACKET, "ADD_EXPR MOD_EXPR_TAIL");
        putInTable(NodeType.MOD_EXPR, TokenType.NOT_OPERATOR, "ADD_EXPR MOD_EXPR_TAIL");
        putInTable(NodeType.MOD_EXPR, TokenType.IN_KEYWORD, "ADD_EXPR MOD_EXPR_TAIL");
        putInTable(NodeType.MOD_EXPR, TokenType.CHAR_CONSTANT, "ADD_EXPR MOD_EXPR_TAIL");
        putInTable(NodeType.MOD_EXPR, TokenType.BOOL_CONSTANT, "ADD_EXPR MOD_EXPR_TAIL");
        putInTable(NodeType.MOD_EXPR, TokenType.INT_CONSTANT, "ADD_EXPR MOD_EXPR_TAIL");
        putInTable(NodeType.MOD_EXPR, TokenType.FLOAT_CONSTANT, "ADD_EXPR MOD_EXPR_TAIL");
    }

    public void fillForModExprTail() {
        putInTable(NodeType.MOD_EXPR_TAIL, TokenType.CLOSE_CURLY_BRACKET, "");
        putInTable(NodeType.MOD_EXPR_TAIL, TokenType.EXPRESSION_OPERATOR, "");
        putInTable(NodeType.MOD_EXPR_TAIL, TokenType.SEMI_COLON, "");
        putInTable(NodeType.MOD_EXPR_TAIL, TokenType.CLOSE_BRACKET, "");
        putInTable(NodeType.MOD_EXPR_TAIL, TokenType.COMMA, "");
        putInTable(NodeType.MOD_EXPR_TAIL, TokenType.GREATER_EQUAL_OPERATOR, "");
        putInTable(NodeType.MOD_EXPR_TAIL, TokenType.LESS_EQUAL_OPERATOR, "");
        putInTable(NodeType.MOD_EXPR_TAIL, TokenType.LESS_OPERATOR, "");
        putInTable(NodeType.MOD_EXPR_TAIL, TokenType.GREATER_OPERATOR, "");
        putInTable(NodeType.MOD_EXPR_TAIL, TokenType.EQUAL_OPERATOR, "");
        putInTable(NodeType.MOD_EXPR_TAIL, TokenType.NOT_EQUAL_OPERATOR, "");
        putInTable(NodeType.MOD_EXPR_TAIL, TokenType.MODULO_OPERATOR, "% ADD_EXPR MOD_EXPR_TAIL");
        putInTable(NodeType.MOD_EXPR_TAIL, TokenType.CLOSE_SQUARE_BRACKET, "");
    }

    public void fillForAddExpr() {
        putInTable(NodeType.ADD_EXPR, TokenType.IDENTIFIER, "MUL_EXPR ADD_EXPR_TAIL");
        putInTable(NodeType.ADD_EXPR, TokenType.OPEN_BRACKET, "MUL_EXPR ADD_EXPR_TAIL");
        putInTable(NodeType.ADD_EXPR, TokenType.NOT_OPERATOR, "MUL_EXPR ADD_EXPR_TAIL");
        putInTable(NodeType.ADD_EXPR, TokenType.IN_KEYWORD, "MUL_EXPR ADD_EXPR_TAIL");
        putInTable(NodeType.ADD_EXPR, TokenType.CHAR_CONSTANT, "MUL_EXPR ADD_EXPR_TAIL");
        putInTable(NodeType.ADD_EXPR, TokenType.BOOL_CONSTANT, "MUL_EXPR ADD_EXPR_TAIL");
        putInTable(NodeType.ADD_EXPR, TokenType.INT_CONSTANT, "MUL_EXPR ADD_EXPR_TAIL");
        putInTable(NodeType.ADD_EXPR, TokenType.FLOAT_CONSTANT, "MUL_EXPR ADD_EXPR_TAIL");
    }

    public void fillForAddExprTail() {
        putInTable(NodeType.ADD_EXPR_TAIL, TokenType.CLOSE_CURLY_BRACKET, "");
        putInTable(NodeType.ADD_EXPR_TAIL, TokenType.EXPRESSION_OPERATOR, "");
        putInTable(NodeType.ADD_EXPR_TAIL, TokenType.SEMI_COLON, "");
        putInTable(NodeType.ADD_EXPR_TAIL, TokenType.COMMA, "");
        putInTable(NodeType.ADD_EXPR_TAIL, TokenType.CLOSE_BRACKET, "");
        putInTable(NodeType.ADD_EXPR_TAIL, TokenType.GREATER_EQUAL_OPERATOR, "");
        putInTable(NodeType.ADD_EXPR_TAIL, TokenType.LESS_EQUAL_OPERATOR, "");
        putInTable(NodeType.ADD_EXPR_TAIL, TokenType.LESS_OPERATOR, "");
        putInTable(NodeType.ADD_EXPR_TAIL, TokenType.GREATER_OPERATOR, "");
        putInTable(NodeType.ADD_EXPR_TAIL, TokenType.EQUAL_OPERATOR, "");
        putInTable(NodeType.ADD_EXPR_TAIL, TokenType.NOT_EQUAL_OPERATOR, "");
        putInTable(NodeType.ADD_EXPR_TAIL, TokenType.MODULO_OPERATOR, "");
        putInTable(NodeType.ADD_EXPR_TAIL, TokenType.ADDITION_OPERATOR, "+ MUL_EXPR ADD_EXPR_TAIL");
        putInTable(NodeType.ADD_EXPR_TAIL, TokenType.SUBTRACTION_OPERATOR, "- MUL_EXPR ADD_EXPR_TAIL");
        putInTable(NodeType.ADD_EXPR_TAIL, TokenType.OR_OPERATOR, "|| MUL_EXPR ADD_EXPR_TAIL");
        putInTable(NodeType.ADD_EXPR_TAIL, TokenType.CLOSE_SQUARE_BRACKET, "");
    }

    public void fillForMulExpr() {
        putInTable(NodeType.MUL_EXPR, TokenType.IDENTIFIER, "PRIMARY MUL_EXPR_TAIL");
        putInTable(NodeType.MUL_EXPR, TokenType.OPEN_BRACKET, "PRIMARY MUL_EXPR_TAIL");
        putInTable(NodeType.MUL_EXPR, TokenType.NOT_OPERATOR, "PRIMARY MUL_EXPR_TAIL");
        putInTable(NodeType.MUL_EXPR, TokenType.IN_KEYWORD, "PRIMARY MUL_EXPR_TAIL");
        putInTable(NodeType.MUL_EXPR, TokenType.CHAR_CONSTANT, "PRIMARY MUL_EXPR_TAIL");
        putInTable(NodeType.MUL_EXPR, TokenType.BOOL_CONSTANT, "PRIMARY MUL_EXPR_TAIL");
        putInTable(NodeType.MUL_EXPR, TokenType.INT_CONSTANT, "PRIMARY MUL_EXPR_TAIL");
        putInTable(NodeType.MUL_EXPR, TokenType.FLOAT_CONSTANT, "PRIMARY MUL_EXPR_TAIL");
    }

    public void fillForMulExprTail() {
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.CLOSE_CURLY_BRACKET, "");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.EXPRESSION_OPERATOR, "");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.SEMI_COLON, "");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.COMMA, "");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.CLOSE_BRACKET, "");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.GREATER_EQUAL_OPERATOR, "");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.LESS_EQUAL_OPERATOR, "");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.LESS_OPERATOR, "");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.GREATER_OPERATOR, "");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.EQUAL_OPERATOR, "");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.NOT_EQUAL_OPERATOR, "");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.MODULO_OPERATOR, "");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.ADDITION_OPERATOR, "");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.SUBTRACTION_OPERATOR, "");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.OR_OPERATOR, "");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.MULTIPLICATION_OPERATOR, "* PRIMARY MUL_EXPR_TAIL");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.DIVISION_OPERATOR, "/ PRIMARY MUL_EXPR_TAIL");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.AND_OPERATOR, "&& PRIMARY MUL_EXPR_TAIL");
        putInTable(NodeType.MUL_EXPR_TAIL, TokenType.CLOSE_SQUARE_BRACKET, "");
    }

    public void fillForPrimary() {
        putInTable(NodeType.PRIMARY, TokenType.IDENTIFIER, "INSTANCE");
        putInTable(NodeType.PRIMARY, TokenType.OPEN_BRACKET, "( EXPRESSION )");
        putInTable(NodeType.PRIMARY, TokenType.NOT_OPERATOR, "! PRIMARY");
        putInTable(NodeType.PRIMARY, TokenType.IN_KEYWORD, "in");
        putInTable(NodeType.PRIMARY, TokenType.CHAR_CONSTANT, "PRIMITIVE_CONSTANT");
        putInTable(NodeType.PRIMARY, TokenType.BOOL_CONSTANT, "PRIMITIVE_CONSTANT");
        putInTable(NodeType.PRIMARY, TokenType.INT_CONSTANT, "PRIMITIVE_CONSTANT");
        putInTable(NodeType.PRIMARY, TokenType.FLOAT_CONSTANT, "PRIMITIVE_CONSTANT");
    }

    public void fillForInstance() {
        putInTable(NodeType.INSTANCE, TokenType.IDENTIFIER, "identifier INSTANCE_TAIL");
    }

    public void fillForInstanceTail() {
        putInTable(NodeType.INSTANCE_TAIL, TokenType.EXPRESSION_OPERATOR, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.SEMI_COLON, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.COMMA, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.OPEN_BRACKET, "( EXPR_LIST )");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.CLOSE_BRACKET, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.OPEN_SQUARE_BRACKET, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.CLOSE_SQUARE_BRACKET, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.GREATER_EQUAL_OPERATOR, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.LESS_EQUAL_OPERATOR, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.LESS_OPERATOR, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.GREATER_OPERATOR, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.EQUAL_OPERATOR, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.NOT_EQUAL_OPERATOR, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.MODULO_OPERATOR, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.ADDITION_OPERATOR, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.SUBTRACTION_OPERATOR, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.OR_OPERATOR, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.MULTIPLICATION_OPERATOR, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.DIVISION_OPERATOR, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.AND_OPERATOR, "ASSIGNABLE_INSTANCE_TAIL");
        putInTable(NodeType.INSTANCE_TAIL, TokenType.DOT, "ASSIGNABLE_INSTANCE_TAIL");
    }

    public void fillForAssignableInstance() {
        putInTable(NodeType.ASSIGNABLE_INSTANCE, TokenType.IDENTIFIER, "identifier ASSIGNABLE_INSTANCE_TAIL");
    }

    public void fillForAssignableInstanceTail() {
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.EXPRESSION_OPERATOR, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.SEMI_COLON, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.COMMA, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.CLOSE_BRACKET, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.OPEN_SQUARE_BRACKET, "[ MOD_EXPR ] DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.CLOSE_SQUARE_BRACKET, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.GREATER_EQUAL_OPERATOR, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.LESS_EQUAL_OPERATOR, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.LESS_OPERATOR, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.GREATER_OPERATOR, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.EQUAL_OPERATOR, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.NOT_EQUAL_OPERATOR, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.MODULO_OPERATOR, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.ADDITION_OPERATOR, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.SUBTRACTION_OPERATOR, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.OR_OPERATOR, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.MULTIPLICATION_OPERATOR, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.DIVISION_OPERATOR, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.AND_OPERATOR, "DOT_TAIL");
        putInTable(NodeType.ASSIGNABLE_INSTANCE_TAIL, TokenType.DOT, "DOT_TAIL");
    }

    public void fillForDotTail() {
        putInTable(NodeType.DOT_TAIL, TokenType.EXPRESSION_OPERATOR, "");
        putInTable(NodeType.DOT_TAIL, TokenType.SEMI_COLON, "");
        putInTable(NodeType.DOT_TAIL, TokenType.COMMA, "");
        putInTable(NodeType.DOT_TAIL, TokenType.CLOSE_BRACKET, "");
        putInTable(NodeType.DOT_TAIL, TokenType.CLOSE_SQUARE_BRACKET, "");
        putInTable(NodeType.DOT_TAIL, TokenType.GREATER_EQUAL_OPERATOR, "");
        putInTable(NodeType.DOT_TAIL, TokenType.LESS_EQUAL_OPERATOR, "");
        putInTable(NodeType.DOT_TAIL, TokenType.LESS_OPERATOR, "");
        putInTable(NodeType.DOT_TAIL, TokenType.GREATER_OPERATOR, "");
        putInTable(NodeType.DOT_TAIL, TokenType.EQUAL_OPERATOR, "");
        putInTable(NodeType.DOT_TAIL, TokenType.NOT_EQUAL_OPERATOR, "");
        putInTable(NodeType.DOT_TAIL, TokenType.MODULO_OPERATOR, "");
        putInTable(NodeType.DOT_TAIL, TokenType.ADDITION_OPERATOR, "");
        putInTable(NodeType.DOT_TAIL, TokenType.SUBTRACTION_OPERATOR, "");
        putInTable(NodeType.DOT_TAIL, TokenType.OR_OPERATOR, "");
        putInTable(NodeType.DOT_TAIL, TokenType.MULTIPLICATION_OPERATOR, "");
        putInTable(NodeType.DOT_TAIL, TokenType.DIVISION_OPERATOR, "");
        putInTable(NodeType.DOT_TAIL, TokenType.AND_OPERATOR, "");
        putInTable(NodeType.DOT_TAIL, TokenType.DOT, ". identifier ASSIGNABLE_INSTANCE_TAIL");
    }

    public void fillForExprList() {
        putInTable(NodeType.EXPR_LIST, TokenType.IDENTIFIER, "EXPRESSION EXPR_LIST_TAIL");
        putInTable(NodeType.EXPR_LIST, TokenType.OPEN_BRACKET, "EXPRESSION EXPR_LIST_TAIL");
        putInTable(NodeType.EXPR_LIST, TokenType.CLOSE_BRACKET, "");
        putInTable(NodeType.EXPR_LIST, TokenType.NOT_OPERATOR, "EXPRESSION EXPR_LIST_TAIL");
        putInTable(NodeType.EXPR_LIST, TokenType.IN_KEYWORD, "EXPRESSION EXPR_LIST_TAIL");
        putInTable(NodeType.EXPR_LIST, TokenType.CHAR_CONSTANT, "EXPRESSION EXPR_LIST_TAIL");
        putInTable(NodeType.EXPR_LIST, TokenType.BOOL_CONSTANT, "EXPRESSION EXPR_LIST_TAIL");
        putInTable(NodeType.EXPR_LIST, TokenType.INT_CONSTANT, "EXPRESSION EXPR_LIST_TAIL");
        putInTable(NodeType.EXPR_LIST, TokenType.FLOAT_CONSTANT, "EXPRESSION EXPR_LIST_TAIL");
    }

    public void fillForExprListTail() {
        putInTable(NodeType.EXPR_LIST_TAIL, TokenType.COMMA, ", EXPRESSION EXPR_LIST_TAIL");
        putInTable(NodeType.EXPR_LIST_TAIL, TokenType.CLOSE_BRACKET, "");
    }

    public void fillForReturnType() {
        putInTable(NodeType.RETURN_TYPE, TokenType.BAG_KEYWORD, "TYPE");
        putInTable(NodeType.RETURN_TYPE, TokenType.VOID_KEYWORD, "void");
        putInTable(NodeType.RETURN_TYPE, TokenType.FLOAT_KEYWORD, "TYPE");
        putInTable(NodeType.RETURN_TYPE, TokenType.INT_KEYWORD, "TYPE");
        putInTable(NodeType.RETURN_TYPE, TokenType.CHAR_KEYWORD, "TYPE");
        putInTable(NodeType.RETURN_TYPE, TokenType.BOOL_KEYWORD, "TYPE");
    }

    public void fillForType() {
        putInTable(NodeType.TYPE, TokenType.BAG_KEYWORD, "PRIMITIVE_TYPE ARRAY_EMPTY_EXTENSION");
        putInTable(NodeType.TYPE, TokenType.FLOAT_KEYWORD, "PRIMITIVE_TYPE ARRAY_EMPTY_EXTENSION");
        putInTable(NodeType.TYPE, TokenType.INT_KEYWORD, "PRIMITIVE_TYPE ARRAY_EMPTY_EXTENSION");
        putInTable(NodeType.TYPE, TokenType.CHAR_KEYWORD, "PRIMITIVE_TYPE ARRAY_EMPTY_EXTENSION");
        putInTable(NodeType.TYPE, TokenType.BOOL_KEYWORD, "PRIMITIVE_TYPE ARRAY_EMPTY_EXTENSION");
    }

    public void fillForArrayEmptyExtension() {
        putInTable(NodeType.ARRAY_EMPTY_EXTENSION, TokenType.OPEN_CURLY_BRACKET, "");
        putInTable(NodeType.ARRAY_EMPTY_EXTENSION, TokenType.CLOSE_CURLY_BRACKET, "");
        putInTable(NodeType.ARRAY_EMPTY_EXTENSION, TokenType.SEMI_COLON, "");
        putInTable(NodeType.ARRAY_EMPTY_EXTENSION, TokenType.COMMA, "");
        putInTable(NodeType.ARRAY_EMPTY_EXTENSION, TokenType.CLOSE_BRACKET, "");
        putInTable(NodeType.ARRAY_EMPTY_EXTENSION, TokenType.OPEN_SQUARE_BRACKET, "[ ]");
    }

    public void fillForPrimitiveType() {
        putInTable(NodeType.PRIMITIVE_TYPE, TokenType.BAG_KEYWORD, "bag identifier");
        putInTable(NodeType.PRIMITIVE_TYPE, TokenType.FLOAT_KEYWORD, "float");
        putInTable(NodeType.PRIMITIVE_TYPE, TokenType.INT_KEYWORD, "int");
        putInTable(NodeType.PRIMITIVE_TYPE, TokenType.CHAR_KEYWORD, "char");
        putInTable(NodeType.PRIMITIVE_TYPE, TokenType.BOOL_KEYWORD, "bool");
    }

    public void fillForPrimitiveConstant() {
        putInTable(NodeType.PRIMITIVE_CONSTANT, TokenType.CHAR_CONSTANT, "char_constant");
        putInTable(NodeType.PRIMITIVE_CONSTANT, TokenType.BOOL_CONSTANT, "bool_constant");
        putInTable(NodeType.PRIMITIVE_CONSTANT, TokenType.INT_CONSTANT, "int_constant");
        putInTable(NodeType.PRIMITIVE_CONSTANT, TokenType.FLOAT_CONSTANT, "float_constant");
    }
}
