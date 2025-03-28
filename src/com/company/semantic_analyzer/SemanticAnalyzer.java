package com.company.semantic_analyzer;

import com.company.dev_exceptions.GeneralDevException;
import com.company.exceptions.symbol_table.*;
import com.company.exceptions.*;
import com.company.model.Pair;
import com.company.parser.abstract_syntax_tree.ASTNodes;
import com.company.parser.abstract_syntax_tree.AbstractSyntaxTree;
import com.company.parser.abstract_syntax_tree.helpers.PrimitiveToTypeConverter;
import com.company.parser.abstract_syntax_tree.helpers.TypeToTypeConverter;
import com.company.symbol_table.SymbolTable;
import com.company.symbol_table.variable_types.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemanticAnalyzer {
    SymbolTable symbolTable;
    PrimitiveToTypeConverter primitiveToTypeConverter;
    TypeToTypeConverter typeToTypeConverter;

    public SemanticAnalyzer() {
        this.symbolTable = new SymbolTable();
        primitiveToTypeConverter = new PrimitiveToTypeConverter();
        typeToTypeConverter = new TypeToTypeConverter();
    }

    private int addScopeAndTimeToNodes(ASTNodes.ASTNode node, int scope, int time) throws ScopeNotFoundException {
        int currScope = scope;
        int nextTime = time + 1;
        if (node instanceof ASTNodes.FunctionDefinition || node instanceof ASTNodes.If || node instanceof ASTNodes.Elif ||
            node instanceof ASTNodes.Else || node instanceof ASTNodes.While) {
            currScope = symbolTable.addScope(scope);
        }
        node.scope = currScope;
        node.time = time;
        for(ASTNodes.ASTNode child : node.getChildren()) {
            if (child != null) {
                child.parent = node;
                nextTime = addScopeAndTimeToNodes(child, currScope, nextTime);
            }
        }
        return nextTime;
    }

    private void defineConstants(ASTNodes.Definition node) throws ConstantDefinedMultipleTimesException {
        if (node == null) {
            return;
        }
        ASTNodes.DefinitionInstance instance = (ASTNodes.DefinitionInstance) node.firstDefinition;
        while (instance != null) {
            VarType type = primitiveToTypeConverter.apply((ASTNodes.Constant) instance.primitiveConstant);
            try {
                symbolTable.defineConstant(instance.identifier, type, ((ASTNodes.Constant) instance.primitiveConstant).value);
            } catch (ConstantAlreadyDefinedException e) {
                throw new ConstantDefinedMultipleTimesException(instance.identifier, instance.line);
            }
            instance = (ASTNodes.DefinitionInstance) instance.nextDefinition;
        }
    }

    private void defineFunction(ASTNodes.FunctionDefinition node) throws VariableDefinedMultipleTimesException, ScopeNotFoundException, FunctionDefinedMultipleTimesException {
        ASTNodes.FunctionParam param = (ASTNodes.FunctionParam) node.paramList;
        List<SymbolTable.FunParam> params = new ArrayList<>();
        while (param != null) {
            VarType type = typeToTypeConverter.apply((ASTNodes.Type) param.type);
            params.add(new SymbolTable.FunParam(param.identifier, type));
            try {
                symbolTable.declareVar(node.scope, param.identifier, type, node.time, true);
            } catch (VariableAlreadyDefinedException|ConstantAlreadyDefinedException e) {
                throw new VariableDefinedMultipleTimesException(param.identifier, param.line);
            }
            param = (ASTNodes.FunctionParam) param.nextParam;
        }
        VarType returnType = typeToTypeConverter.apply((ASTNodes.TypeOrVoid) node.returnType);
        node.returnResultType = returnType;
        try {
            symbolTable.defineFunction(node.functionName, params, returnType, node.scope);
        } catch (FunctionAlreadyDefinedException e) {
            throw new FunctionDefinedMultipleTimesException(node.functionName, node.line);
        }

    }

    private void defineBag(ASTNodes.BagDefinition node) throws BagDefinedMultipleTimesException {
        ASTNodes.BagParam param = (ASTNodes.BagParam) node.paramList;
        List<SymbolTable.BagParam> params = new ArrayList<>();
        while (param != null) {
            VarType type = typeToTypeConverter.apply((ASTNodes.Type) param.type);
            params.add(new SymbolTable.BagParam(param.identifier, type));
            param = (ASTNodes.BagParam) param.nextParam;
        }

        try {
            symbolTable.defineBag(node.bagName, params);
        } catch (BagAlreadyDefinedException e) {
            throw new BagDefinedMultipleTimesException(node.bagName, node.line);
        }
    }

    private void defineConstructs(ASTNodes.ConstructList node) throws FunctionDefinedMultipleTimesException, ScopeNotFoundException, BagDefinedMultipleTimesException, VariableDefinedMultipleTimesException {
        if (node == null) {
            return;
        }

        if (node.construct instanceof ASTNodes.FunctionDefinition) {
            defineFunction((ASTNodes.FunctionDefinition) node.construct);
        } else {
            defineBag((ASTNodes.BagDefinition) node.construct);
        }
        defineConstructs((ASTNodes.ConstructList) node.nextConstruct);
    }

    private void analyzeProgram(ASTNodes.Program node) throws Exception {
        defineConstants((ASTNodes.Definition) node.definition);
        defineConstructs((ASTNodes.ConstructList) node.constructList);
        analyzeConstructList((ASTNodes.ConstructList) node.constructList);
    }

    private void analyzeConstructList(ASTNodes.ConstructList node) throws Exception {
        if (node == null) {
            return;
        }
        if (node.construct instanceof ASTNodes.FunctionDefinition) {
            analyzeFunction((ASTNodes.FunctionDefinition) node.construct);
        }
        analyzeConstructList((ASTNodes.ConstructList) node.nextConstruct);
    }

    private void analyzeFunction(ASTNodes.FunctionDefinition node) throws Exception {
        analyzeStatement((ASTNodes.Statement) node.firstStatement);
    }

    private void analyzeStatement(ASTNodes.Statement node) throws Exception {
        if (node == null) {
            return;
        }
        if (node.statement instanceof ASTNodes.While) {
            analyzeWhileStatement((ASTNodes.While) node.statement);
        } else if (node.statement instanceof ASTNodes.If) {
            analyzeIfStatement((ASTNodes.If) node.statement);
        } else if (node.statement instanceof ASTNodes.ExpressionList) {
            analyzeExpressionList((ASTNodes.ExpressionList) node.statement);
        } else if (node.statement instanceof ASTNodes.DefineVar) {
            analyzeDefineVar((ASTNodes.DefineVar) node.statement);
        } else if (node.statement instanceof ASTNodes.Input) {
            analyzeInputStatement((ASTNodes.Input) node.statement);
        } else if (node.statement instanceof ASTNodes.Output){
            analyzeOutputStatement((ASTNodes.Output) node.statement);
        } else if (node.statement instanceof ASTNodes.AllocArr) {
            analyzeAllocArrStatement((ASTNodes.AllocArr) node.statement);
        } else if (node.statement instanceof ASTNodes.FreeInstance) {
            analyzeFreeArrStatement((ASTNodes.FreeInstance) node.statement);
        } else if (node.statement instanceof ASTNodes.Return) {
            analyzeReturnStatement((ASTNodes.Return) node.statement, null);
        }
        analyzeStatement((ASTNodes.Statement) node.nextStatement);
    }

    private void analyzeWhileStatement(ASTNodes.While node) throws Exception {
        VarType type = analyzeExpression(node.condition, null, node, "condition");
        if (!type.equals(new BoolType())) {
            throw new TypeMismatchException(new BoolType(), type, node.condition.line);
        }
        analyzeStatement((ASTNodes.Statement) node.firstStatement);
    }

    private void analyzeIfStatement(ASTNodes.If node) throws Exception {
        VarType type = analyzeExpression(node.condition, null, node, "condition");
        if (!type.equals(new BoolType())) {
            throw new TypeMismatchException(new BoolType(), type, node.condition.line);
        }
        analyzeStatement((ASTNodes.Statement) node.firstStatement);
        analyzeElse(node.elif);
    }

    private void analyzeElse(ASTNodes.ASTNode node) throws Exception {
        if (node == null) {
            return;
        }
        if (node instanceof ASTNodes.Elif) {
            VarType type = analyzeExpression(((ASTNodes.Elif) node).condition, null, node, "condition");
            if (!type.equals(new BoolType())) {
                throw new TypeMismatchException(new BoolType(), type, ((ASTNodes.Elif) node).condition.line);
            }
            analyzeStatement((ASTNodes.Statement) ((ASTNodes.Elif) node).firstStatement);
            analyzeElse(((ASTNodes.Elif) node).elif);
        } else {
            analyzeStatement((ASTNodes.Statement) ((ASTNodes.Else) node).firstStatement);
        }
    }

    private VarType analyzeExpressionList(ASTNodes.ExpressionList node) throws Exception {
        if (node == null) {
            return null;
        }
        VarType type = analyzeExpressionList((ASTNodes.ExpressionList) node.nextExprList);

        if (node.exprOrCloser instanceof ASTNodes.Return) {
            analyzeReturnStatement((ASTNodes.Return) node.exprOrCloser, type);
            return null;
        } else if (node.exprOrCloser instanceof ASTNodes.Eq) {
            analyzeEqStatement((ASTNodes.Eq) node.exprOrCloser, type);
            return null;
        } else {
            return analyzeExpression(node.exprOrCloser, type, node, "exprOrCloser");
        }
    }

    private void analyzeDefineVar(ASTNodes.DefineVar node) throws ScopeNotFoundException, VariableDefinedMultipleTimesException {
        VarType type = typeToTypeConverter.apply((ASTNodes.Type) node.type);
        try {
            symbolTable.declareVar(node.scope, node.identifier, type, node.time, false);
        } catch (VariableAlreadyDefinedException|ConstantAlreadyDefinedException e) {
            throw new VariableDefinedMultipleTimesException(node.identifier, node.line);
        }
    }

    private void analyzeAllocArrStatement(ASTNodes.AllocArr node) throws Exception {
        VarType type = typeToTypeConverter.apply((ASTNodes.Type) node.type);

        var expressionType = analyzeExpression(node.expression, null, node, "expression");
        if (!expressionType.equals(new IntType())) {
            throw new TypeMismatchException(new IntType(), expressionType, node.expression.line);
        }

        var varRez = analyzeVariable((ASTNodes.Variable) node.assignableInstance);
        if (varRez.first instanceof SymbolTable.ConstInfo) {
            throw new AttemptToChangeConstValueException(varRez.first.name, node.assignableInstance.line);
        }
        var expectedType = VarType.withArrayExt(type);

        if (!expectedType.equals(varRez.second)) {
            throw new TypeMismatchException(expectedType, varRez.second, node.assignableInstance.line);
        }

    }

    private void analyzeFreeArrStatement(ASTNodes.FreeInstance node) throws Exception {

        var varRez = analyzeVariable((ASTNodes.Variable) node.assignableInstance);
        if (varRez.first instanceof SymbolTable.ConstInfo) {
            throw new AttemptToChangeConstValueException(varRez.first.name, node.assignableInstance.line);
        }

        var varType = varRez.second;

        if (!varType.hasArrExt & !(varType instanceof BagType)) {
            throw new AttemptToFreeAPrimitiveValueException(varType, node.assignableInstance.line);
        }
    }

    private Pair<SymbolTable.VarOrConstInfo, VarType> analyzeVariable(ASTNodes.Variable node) throws Exception {
        var varOrConstInfo = symbolTable.varLookup(node.scope, node.name, node.time);

        if (varOrConstInfo instanceof SymbolTable.ConstInfo) {
            if (node.callExtension != null) {
                throw new AttemptedToDrillConstantException(varOrConstInfo.name, node.line);
            }

            ASTNodes.Constant newNode = null;
            if (varOrConstInfo.type instanceof IntType) {
                newNode = new ASTNodes.IntConstant();
                newNode.value = ((SymbolTable.ConstInfo) varOrConstInfo).value;
            } else if (varOrConstInfo.type instanceof BoolType) {
                newNode = new ASTNodes.BoolConstant();
                newNode.value = ((SymbolTable.ConstInfo) varOrConstInfo).value;
            } else if (varOrConstInfo.type instanceof FloatType) {
                newNode = new ASTNodes.FloatConstant();
                newNode.value = ((SymbolTable.ConstInfo) varOrConstInfo).value;
            } else if (varOrConstInfo.type instanceof CharType) {
                newNode = new ASTNodes.CharConstant();
                newNode.value = ((SymbolTable.ConstInfo) varOrConstInfo).value;
            }
            node.parent = newNode;

            return new Pair<>(varOrConstInfo, varOrConstInfo.type);
        }

        var result = varOrConstInfo.type;
        if (node.callExtension instanceof ASTNodes.ArrayCallExtension) {
            result = analyzeArrayCallExtension((ASTNodes.ArrayCallExtension) node.callExtension, varOrConstInfo.type);
        } else if (node.callExtension instanceof ASTNodes.BagCallExtension) {
            result = analyzeBagCallExtension((ASTNodes.BagCallExtension) node.callExtension, varOrConstInfo.type);
        }
        return new Pair<>(varOrConstInfo, result);
    }

    private VarType analyzeArrayCallExtension(ASTNodes.ArrayCallExtension node, VarType calledWithType) throws Exception {
        if (calledWithType == null) {
            throw new GeneralDevException("Invalid call with calledWithType=null of analyzeArrayCallExtension");
        }
        if (!calledWithType.hasArrExt) {
            throw new AttemptedToDrillUndrillableTypeException(calledWithType, node.line);
        }

        var drilledType = VarType.arrayDrilled(calledWithType);
        var expressionType = analyzeExpression(node.expression, null, node, "expression");
        if (!expressionType.equals(new IntType())) {
            throw new TypeMismatchException(new IntType(), expressionType, node.expression.line);
        }

        if (node.callExtension != null) {
            return analyzeBagCallExtension((ASTNodes.BagCallExtension) node.callExtension, drilledType);
        }
        return drilledType;
    }

    private VarType analyzeBagCallExtension(ASTNodes.BagCallExtension node, VarType calledWithType) throws Exception {
        if (calledWithType == null) {
            throw new GeneralDevException("Invalid call with calledWithType=null of analyzeBagCallExtension");
        }

        if (!(calledWithType instanceof BagType) || calledWithType.hasArrExt) {
            throw new AttemptedToDrillUndrillableTypeException(calledWithType, node.line);
        }

        var bagInfo = symbolTable.bagLookup(((BagType) calledWithType).name);

        var fieldType = bagInfo.getField(node.fieldName);

        if (fieldType == null) {
            throw new FieldDoesntExistOnBagException(bagInfo.bagName, node.fieldName, node.line);
        }

        if (node.callExtension instanceof ASTNodes.ArrayCallExtension) {
            return analyzeArrayCallExtension((ASTNodes.ArrayCallExtension) node.callExtension, fieldType.second);
        } else if (node.callExtension instanceof ASTNodes.BagCallExtension) {
            return analyzeBagCallExtension((ASTNodes.BagCallExtension) node.callExtension, fieldType.second);
        } else {
            return fieldType.second;
        }
    }

    private void analyzeInputStatement(ASTNodes.Input node) throws Exception {
        var varRez = analyzeVariable((ASTNodes.Variable) node.assignableInstance);

        if (varRez.first instanceof SymbolTable.ConstInfo) {
            throw new AttemptToChangeConstValueException(varRez.first.name, node.assignableInstance.line);
        }

        if (!VarType.isPrimitive(varRez.second)) {
            throw new AttemptedToInputNonPrimitiveType(varRez.second, node.assignableInstance.line);
        }
    }

    private void analyzeOutputStatement(ASTNodes.Output node) throws Exception {
        var expressionType = analyzeExpression(node.expression, null, node, "expression");
        if (!VarType.isPrimitive(expressionType)) {
            throw new AttemptedToOutputNonPrimitiveType(expressionType, node.expression.line);
        }
    }

    private void analyzeReturnStatement(ASTNodes.Return node, VarType type) throws InvalidReturnTypeException {
        if (type == null) {
            type = new VoidType();
        }

        ASTNodes.ASTNode function = node.parent;
        while (!(function instanceof ASTNodes.FunctionDefinition)) {
            function = function.parent;
        }
        VarType functionReturnType = symbolTable.getFunctionReturnType(((ASTNodes.FunctionDefinition) function).functionName);

        if (!functionReturnType.equals(type)) {
            throw new InvalidReturnTypeException(functionReturnType, type, node.line);
        }
    }

    private void analyzeEqStatement(ASTNodes.Eq node, VarType type) throws Exception {
         var varRez = analyzeVariable((ASTNodes.Variable) node.assignableInstance);
         var variable = varRez.first;
         var varType = varRez.second;

         if (variable instanceof SymbolTable.ConstInfo) {
             throw new AttemptToChangeConstValueException(variable.name, node.assignableInstance.line);
         }
         if (!varType.equals(type)) {
             throw new TypeMismatchException(type, varType, node.assignableInstance.line);
         }
    }

    private void swapConstVariableWithVariable(ASTNodes.ASTNode parent, ASTNodes.ASTNode nodeBeingReplaced, String fieldName, SymbolTable.ConstInfo constInfo) throws GeneralDevException {
        ASTNodes.Constant newNode = null;
        if (constInfo.type instanceof IntType) {
            newNode = new ASTNodes.IntConstant();
            newNode.value = constInfo.value;
        } else if (constInfo.type instanceof BoolType) {
            newNode = new ASTNodes.BoolConstant();
            newNode.value = constInfo.value;
        } else if (constInfo.type instanceof FloatType) {
            newNode = new ASTNodes.FloatConstant();
            newNode.value = constInfo.value;
        } else if (constInfo.type instanceof CharType) {
            newNode = new ASTNodes.CharConstant();
            newNode.value = constInfo.value;
        }

        newNode.time = nodeBeingReplaced.time;
        newNode.parent = nodeBeingReplaced.parent;
        newNode.scope = nodeBeingReplaced.scope;
        newNode.returnResultType = nodeBeingReplaced.returnResultType;

        try {
            Field field = parent.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(parent, newNode);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new GeneralDevException("Field name doesnt exist");
        }
    }

    private VarType analyzeExpression(ASTNodes.ASTNode node, VarType inValue, ASTNodes.ASTNode parent, String calledWithField) throws Exception {
        if (node instanceof ASTNodes.BinaryOperator) {
            return analyzeBinaryOperator((ASTNodes.BinaryOperator) node, inValue);
        } else if (node instanceof ASTNodes.UnaryOperator) {
            return analyzeUnaryOperator((ASTNodes.UnaryOperator) node, inValue);
        } else if (node instanceof ASTNodes.Variable) {
            var rez = analyzeVariable((ASTNodes.Variable) node);
            if (rez.first instanceof SymbolTable.ConstInfo) {
                swapConstVariableWithVariable(parent, node, calledWithField, (SymbolTable.ConstInfo) rez.first);
            }
            return rez.second;
        } else if (node instanceof ASTNodes.FunctionCall) {
            return analyzeFunctionCall((ASTNodes.FunctionCall) node, inValue);
        } else if (node instanceof ASTNodes.Constant) {
            return primitiveToTypeConverter.apply((ASTNodes.Constant) node);
        } else {
            if (inValue == null) {
                throw new InHasNoValueException(node.line);
            }
            return inValue;
        }
    }

    private VarType analyzeFunctionCall(ASTNodes.FunctionCall node, VarType inValue) throws Exception {
        List<VarType> paramTypes = new ArrayList<>();
        ASTNodes.FunctionCallArgument arg = (ASTNodes.FunctionCallArgument) node.firstArgument;
        while (arg != null) {
            paramTypes.add(analyzeExpression(arg.expression, inValue, arg, "expression"));
            arg = (ASTNodes.FunctionCallArgument) arg.next;
        }
        SymbolTable.FunInfo funInfo = symbolTable.funLookup(node.identifier);

        if (funInfo.params.size() != paramTypes.size()) {
            throw new FunctionDoesntExistException(node.identifier, paramTypes, node.line);
        }
        for (int i = 0; i < funInfo.params.size(); i++) {
            if (!paramTypes.get(i).equals(funInfo.params.get(i).type)) {
                throw new FunctionDoesntExistException(node.identifier, paramTypes, node.line);
            }
        }

        return funInfo.returnType;
    }

    private VarType analyzeUnaryOperator(ASTNodes.UnaryOperator node, VarType inValue) throws Exception {
        VarType type = analyzeExpression(node.left, inValue, node, "left");
        if (!type.equals(new BoolType())) {
            throw new TypeMismatchException(new BoolType(), type, node.line);
        }
        return type;
    }

    private VarType analyzeBinaryOperator(ASTNodes.BinaryOperator node, VarType inValue) throws Exception {
        VarType left = analyzeExpression(node.left, inValue, node, "left");
        VarType right = analyzeExpression(node.right, inValue, node, "right");
        Matcher arithmeticSign = Pattern.compile("[+\\-*/]").matcher(node.operator);
        Matcher moduloSign = Pattern.compile("%").matcher(node.operator);
        Matcher comparatorSign = Pattern.compile("(<=|<|>=|>|==|!=)").matcher(node.operator);
        if (arithmeticSign.matches()) {
            if (left.equals(new IntType()) && right.equals(new IntType())) {
                return new IntType(false);
            } else if (left.equals(new IntType()) && right.equals(new FloatType())) {
                return new FloatType(false);
            } else if (left.equals(new FloatType()) && right.equals(new IntType())) {
                return new FloatType();
            } else if (left.equals(new FloatType()) && right.equals(new FloatType())) {
                return new FloatType();
            }
        } else if (moduloSign.matches()) {
            if (left.equals(new IntType()) && right.equals(new IntType())) {
                return new IntType();
            }
        } else if (comparatorSign.matches()) {
            if (left.equals(new IntType()) && right.equals(new IntType())) {
                return new BoolType(false);
            } else if (left.equals(new IntType()) && right.equals(new FloatType())) {
                return new BoolType(false);
            } else if (left.equals(new FloatType()) && right.equals(new IntType())) {
                return new BoolType();
            } else if (left.equals(new FloatType()) && right.equals(new FloatType())) {
                return new BoolType();
            }
        } else {
            if (left.equals(new BoolType()) && right.equals(new BoolType())) {
                return new BoolType();
            }
        }
        throw new InvalidTypesForBinaryOperatorException(node.operator, left, right, node.line);
    }

    private void createFunctionTree(ASTNodes.ASTNode node, FunctionNode curr) {
        if (node == null) {
            return;
        }
        FunctionNode next = curr;
        if (node instanceof ASTNodes.If) {
            next = new FunctionNode();
            curr.firstChild = next;
            next.parent = curr;
        } else if (node instanceof ASTNodes.Elif) {
            next = new FunctionNode();
            curr.neighbor = next;
            next.parent = curr.parent;
        } else if (node instanceof ASTNodes.Else) {
            next = new FunctionNode();
            curr.neighbor = next;
            next.parent = curr.parent;
            next.isElse = true;
        } else if (node instanceof ASTNodes.Return) {
            curr.returns = true;
        }

        for (ASTNodes.ASTNode child : node.getChildren()) {
            createFunctionTree(child, next);
        }
    }

    private boolean checkFunctionTree(FunctionNode node) {
        if (node == null) {
            return false;
        }
        if (node.returns) {
            return true;
        }
        boolean hasElseFlag = false;
        FunctionNode curr = node.firstChild;
        while (curr != null) {
            if (curr.isElse) {
                hasElseFlag = true;
            }
            if (!curr.returns && !checkFunctionTree(curr)) {
                return false;
            }
            curr = curr.neighbor;
        }
        return hasElseFlag;
    }

    private void checkBreakContinueInWhile(ASTNodes.ASTNode node, boolean inWhile) throws BreakNotInLoopException, ContinueNotInLoopException {
        if (node == null) {
            return;
        }
        boolean nowInWhile = inWhile;
        if (node instanceof ASTNodes.While) {
            nowInWhile = true;
        }
        if (node instanceof ASTNodes.Break && !inWhile) {
            throw new BreakNotInLoopException(node.line);
        }
        if (node instanceof ASTNodes.Continue && !inWhile) {
            throw new ContinueNotInLoopException(node.line);
        }

        for (ASTNodes.ASTNode child : node.getChildren()) {
            checkBreakContinueInWhile(child, nowInWhile);
        }
    }

    private void checkAllFunctionsReturn(ASTNodes.Program node) throws NotAllPathsHaveAReturnStatementException {
        var curr = (ASTNodes.ConstructList) node.constructList;
        while (curr != null) {
            if (curr.construct instanceof ASTNodes.FunctionDefinition
                    && !curr.construct.returnResultType.equals(new VoidType()))
            {
                FunctionNode functionNode = new FunctionNode();
                createFunctionTree(curr.construct, functionNode);
                if (!checkFunctionTree(functionNode)) {
                    throw new NotAllPathsHaveAReturnStatementException(((ASTNodes.FunctionDefinition) curr.construct).functionName, curr.construct.line);
                }
            }
            curr = (ASTNodes.ConstructList) curr.nextConstruct;
        }
    }



    public SymbolTable analyze(AbstractSyntaxTree tree) throws Exception {
        addScopeAndTimeToNodes(tree.root, 0, 0);
        analyzeProgram((ASTNodes.Program) tree.root);
        checkAllFunctionsReturn((ASTNodes.Program) tree.root);
        checkBreakContinueInWhile(tree.root, false);
        return symbolTable;
    }
}
