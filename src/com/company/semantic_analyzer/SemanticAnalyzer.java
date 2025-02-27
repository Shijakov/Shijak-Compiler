//package com.company.semantic_analyzer;
//
//import com.company.dev_exceptions.ScopeNotFoundException;
//import com.company.exceptions.*;
//import com.company.parser.abstract_syntax_tree.ASTNodes;
//import com.company.parser.abstract_syntax_tree.AbstractSyntaxTree;
//import com.company.parser.abstract_syntax_tree.helpers.InstantiateTypeToTypeConverter;
//import com.company.parser.abstract_syntax_tree.helpers.PrimitiveToTypeConverter;
//import com.company.parser.abstract_syntax_tree.helpers.TypeToTypeConverter;
//import com.company.symbol_table.SymbolTable;
//import com.company.symbol_table.VarType;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class SemanticAnalyzer {
//    SymbolTable symbolTable;
//    PrimitiveToTypeConverter primitiveToTypeConverter;
//    TypeToTypeConverter typeToTypeConverter;
//    InstantiateTypeToTypeConverter instantiateTypeToTypeConverter;
//
//    public SemanticAnalyzer() {
//        this.symbolTable = new SymbolTable();
//        primitiveToTypeConverter = new PrimitiveToTypeConverter();
//        typeToTypeConverter = new TypeToTypeConverter();
//        instantiateTypeToTypeConverter = new InstantiateTypeToTypeConverter();
//    }
//
//    private void checkType(VarType.PrimitiveType expectedType, boolean expectedArrExt, VarType against) throws TypeMismatchException {
//        if (against.type != expectedType || against.arrExt != expectedArrExt) {
//            throw new TypeMismatchException(new VarType(expectedType, expectedArrExt), against);
//        }
//    }
//
//    private int addScopeAndTimeToNodes(ASTNodes.ASTNode node, int scope, int time) throws ScopeNotFoundException {
//        int currScope = scope;
//        int nextTime = time + 1;
//        if (node instanceof ASTNodes.Function || node instanceof ASTNodes.If || node instanceof ASTNodes.Elif ||
//            node instanceof ASTNodes.Else || node instanceof ASTNodes.While) {
//            currScope = symbolTable.addScope(scope);
//        }
//        node.scope = currScope;
//        node.time = time;
//        for(ASTNodes.ASTNode child : node.getChildren()) {
//            if (child != null) {
//                child.parent = node;
//                nextTime = addScopeAndTimeToNodes(child, currScope, nextTime);
//            }
//        }
//        return nextTime;
//    }
//
//    private void defineConstants(ASTNodes.Definition node) throws ConstantWithSameNameExistsException {
//        if (node == null) {
//            return;
//        }
//        ASTNodes.DefinitionInstance instance = (ASTNodes.DefinitionInstance) node.firstDefinition;
//        while (instance != null) {
//            VarType type = primitiveToTypeConverter.apply(instance.primitiveConstant);
//            symbolTable.defineConstant(instance.identifier, type, type.value);
//            instance = (ASTNodes.DefinitionInstance) instance.nextDefinition;
//        }
//    }
//
//    private void defineFunction(ASTNodes.Function node) throws FunctionDefinedMultipleTimesException, ConstantWithSameNameExistsException, VariableAlreadyDeclaredException, ScopeNotFoundException {
//        ASTNodes.FunctionParam param = (ASTNodes.FunctionParam) node.paramList;
//        List<SymbolTable.FunParam> params = new ArrayList<>();
//        while (param != null) {
//            VarType type = typeToTypeConverter.apply(param.type);
//            params.add(new SymbolTable.FunParam(param.identifier, type));
//            symbolTable.declareVar(node.scope, param.identifier, type, node.time, true);
//            param = (ASTNodes.FunctionParam) param.nextParam;
//        }
//        VarType returnType = typeToTypeConverter.apply(node.returnType);
//        node.returnResultType = returnType;
//        symbolTable.defineFunction(node.name, params, returnType, node.scope);
//    }
//
//    private void defineFunctions(ASTNodes.FunctionList node) throws FunctionDefinedMultipleTimesException, ConstantWithSameNameExistsException, VariableAlreadyDeclaredException, ScopeNotFoundException {
//        if (node == null) {
//            return;
//        }
//        defineFunction((ASTNodes.Function) node.function);
//        defineFunctions((ASTNodes.FunctionList) node.nextFunction);
//    }
//
//    private void analyzeProgram(ASTNodes.Program node) throws ConstantWithSameNameExistsException, FunctionDefinedMultipleTimesException, TypeMismatchException, VariableAlreadyDeclaredException, ScopeNotFoundException, AttemptToChangeConstValueException, VariableNotDeclaredException, InHasNoValueException, InvalidTypesForBinaryOperatorException, FunctionDoesntExistException, InvalidArrayCallException, AttemptToFreeAPrimitiveValueException, InvalidReturnTypeException, CannotAssignValueToPointerException {
//        defineConstants((ASTNodes.Definition) node.definition);
//        defineFunctions((ASTNodes.FunctionList) node.firstFunction);
//        analyzeFunctionList((ASTNodes.FunctionList) node.firstFunction);
//    }
//
//    private void analyzeFunctionList(ASTNodes.FunctionList node) throws TypeMismatchException, ConstantWithSameNameExistsException, VariableAlreadyDeclaredException, ScopeNotFoundException, AttemptToChangeConstValueException, VariableNotDeclaredException, InHasNoValueException, InvalidTypesForBinaryOperatorException, FunctionDoesntExistException, InvalidArrayCallException, AttemptToFreeAPrimitiveValueException, InvalidReturnTypeException, CannotAssignValueToPointerException {
//        if (node == null) {
//            return;
//        }
//        analyzeFunction((ASTNodes.Function) node.function);
//        analyzeFunctionList((ASTNodes.FunctionList) node.nextFunction);
//    }
//
//    private void analyzeFunction(ASTNodes.Function node) throws TypeMismatchException, ConstantWithSameNameExistsException, VariableAlreadyDeclaredException, ScopeNotFoundException, AttemptToChangeConstValueException, VariableNotDeclaredException, InHasNoValueException, InvalidTypesForBinaryOperatorException, FunctionDoesntExistException, InvalidArrayCallException, AttemptToFreeAPrimitiveValueException, InvalidReturnTypeException, CannotAssignValueToPointerException {
//        analyzeStatement((ASTNodes.Statement) node.firstStatement);
//    }
//
//    private void analyzeStatement(ASTNodes.Statement node) throws TypeMismatchException, ConstantWithSameNameExistsException, VariableAlreadyDeclaredException, ScopeNotFoundException, AttemptToChangeConstValueException, VariableNotDeclaredException, InHasNoValueException, InvalidTypesForBinaryOperatorException, FunctionDoesntExistException, InvalidArrayCallException, AttemptToFreeAPrimitiveValueException, InvalidReturnTypeException, CannotAssignValueToPointerException {
//        if (node == null) {
//            return;
//        }
//        if (node.statement instanceof ASTNodes.While) {
//            analyzeWhileStatement((ASTNodes.While) node.statement);
//        } else if (node.statement instanceof ASTNodes.If) {
//            analyzeIfStatement((ASTNodes.If) node.statement);
//        } else if (node.statement instanceof ASTNodes.ExpressionList) {
//            analyzeExpressionList((ASTNodes.ExpressionList) node.statement);
//        } else if (node.statement instanceof ASTNodes.DefineVar) {
//            analyzeDefineVar((ASTNodes.DefineVar) node.statement);
//        } else if (node.statement instanceof ASTNodes.Input) {
//            analyzeInputStatement((ASTNodes.Input) node.statement);
//        } else if (node.statement instanceof ASTNodes.Output){
//            analyzeOutputStatement((ASTNodes.Output) node.statement);
//        } else if (node.statement instanceof ASTNodes.AllocArr) {
//            analyzeAllocArrStatement((ASTNodes.AllocArr) node.statement);
//        } else if (node.statement instanceof ASTNodes.FreeArr) {
//            analyzeFreeArrStatement((ASTNodes.FreeArr) node.statement);
//        } else if (node.statement instanceof ASTNodes.Return) {
//            analyzeReturnStatement((ASTNodes.Return) node.statement, null);
//        }
//        analyzeStatement((ASTNodes.Statement) node.nextStatement);
//    }
//
//    private void analyzeWhileStatement(ASTNodes.While node) throws TypeMismatchException, ConstantWithSameNameExistsException, VariableAlreadyDeclaredException, ScopeNotFoundException, AttemptToChangeConstValueException, VariableNotDeclaredException, InHasNoValueException, InvalidTypesForBinaryOperatorException, FunctionDoesntExistException, InvalidArrayCallException, AttemptToFreeAPrimitiveValueException, InvalidReturnTypeException, CannotAssignValueToPointerException {
//        VarType type = analyzeExpression(node.condition, null);
//        checkType(VarType.PrimitiveType.BOOL, false, type);
//        analyzeStatement((ASTNodes.Statement) node.firstStatement);
//    }
//
//    private void analyzeIfStatement(ASTNodes.If node) throws TypeMismatchException, ConstantWithSameNameExistsException, VariableAlreadyDeclaredException, ScopeNotFoundException, AttemptToChangeConstValueException, VariableNotDeclaredException, InHasNoValueException, InvalidTypesForBinaryOperatorException, FunctionDoesntExistException, InvalidArrayCallException, AttemptToFreeAPrimitiveValueException, InvalidReturnTypeException, CannotAssignValueToPointerException {
//        VarType type = analyzeExpression(node.condition, null);
//        checkType(VarType.PrimitiveType.BOOL, false, type);
//        analyzeStatement((ASTNodes.Statement) node.firstStatement);
//        analyzeElse(node.elif);
//    }
//
//    private void analyzeElse(ASTNodes.ASTNode node) throws TypeMismatchException, ConstantWithSameNameExistsException, VariableAlreadyDeclaredException, ScopeNotFoundException, AttemptToChangeConstValueException, VariableNotDeclaredException, InHasNoValueException, InvalidTypesForBinaryOperatorException, FunctionDoesntExistException, InvalidArrayCallException, AttemptToFreeAPrimitiveValueException, InvalidReturnTypeException, CannotAssignValueToPointerException {
//        if (node == null) {
//            return;
//        }
//        if (node instanceof ASTNodes.Elif) {
//            VarType type = analyzeExpression(((ASTNodes.Elif) node).condition, null);
//            checkType(VarType.PrimitiveType.BOOL, false, type);
//            analyzeStatement((ASTNodes.Statement) ((ASTNodes.Elif) node).firstStatement);
//            analyzeElse(((ASTNodes.Elif) node).elif);
//        } else {
//            analyzeStatement((ASTNodes.Statement) ((ASTNodes.Else) node).firstStatement);
//        }
//    }
//
//    private VarType analyzeExpressionList(ASTNodes.ExpressionList node) throws AttemptToChangeConstValueException, VariableNotDeclaredException, TypeMismatchException, ScopeNotFoundException, InHasNoValueException, InvalidTypesForBinaryOperatorException, FunctionDoesntExistException, InvalidArrayCallException, InvalidReturnTypeException, CannotAssignValueToPointerException {
//        if (node == null) {
//            return null;
//        }
//        VarType type = analyzeExpressionList((ASTNodes.ExpressionList) node.nextExprList);
//
//        if (node.exprOrCloser instanceof ASTNodes.Return) {
//            analyzeReturnStatement((ASTNodes.Return) node.exprOrCloser, type);
//            return null;
//        } else if (node.exprOrCloser instanceof ASTNodes.Eq) {
//            analyzeEqStatement((ASTNodes.Eq) node.exprOrCloser, type);
//            return null;
//        } else {
//            return analyzeExpression(node.exprOrCloser, type);
//        }
//    }
//
//    private void analyzeDefineVar(ASTNodes.DefineVar node) throws ConstantWithSameNameExistsException, VariableAlreadyDeclaredException, ScopeNotFoundException {
//        VarType type = typeToTypeConverter.apply(node.type);
//        symbolTable.declareVar(node.scope, node.identifier, type, node.time, false);
//    }
//
//    private void analyzeAllocArrStatement(ASTNodes.AllocArr node) throws InvalidArrayCallException, FunctionDoesntExistException, VariableNotDeclaredException, InHasNoValueException, TypeMismatchException, InvalidTypesForBinaryOperatorException, ScopeNotFoundException, AttemptToChangeConstValueException {
//        VarType type = instantiateTypeToTypeConverter.apply(node.allocArrType);
//        SymbolTable.VarOrConstInfo identifierType = symbolTable.varLookup(node.scope, node.identifier, node.time);
//        if (identifierType instanceof SymbolTable.ConstInfo) {
//            throw new AttemptToChangeConstValueException(node.identifier);
//        }
//        if (identifierType.type.type != type.type || identifierType.type.arrExt != type.arrExt) {
//            throw new TypeMismatchException(identifierType.type, type);
//        }
//        ASTNodes.InitType initType = (ASTNodes.InitType) node.allocArrType;
//        analyzeArrExtension((ASTNodes.ArrayCallIdx) initType.arrExt);
//    }
//
//    private void analyzeFreeArrStatement(ASTNodes.FreeArr node) throws VariableNotDeclaredException, ScopeNotFoundException, AttemptToChangeConstValueException, AttemptToFreeAPrimitiveValueException {
//        SymbolTable.VarOrConstInfo type = symbolTable.varLookup(node.scope, node.identifier, node.time);
//        if (type instanceof SymbolTable.ConstInfo) {
//            throw new AttemptToChangeConstValueException(node.identifier);
//        }
//        if (!type.type.arrExt) {
//            throw new AttemptToFreeAPrimitiveValueException(node.identifier);
//        }
//    }
//
//    private void analyzeArrExtension(ASTNodes.ArrayCallIdx callIdx) throws VariableNotDeclaredException, InHasNoValueException, TypeMismatchException, ScopeNotFoundException, InvalidTypesForBinaryOperatorException, FunctionDoesntExistException, InvalidArrayCallException {
//        analyzeExpression(callIdx.expression, null);
//    }
//
//    private void analyzeInputStatement(ASTNodes.Input node) throws VariableNotDeclaredException, InHasNoValueException, TypeMismatchException, ScopeNotFoundException, InvalidTypesForBinaryOperatorException, FunctionDoesntExistException, InvalidArrayCallException {
//        analyzeExpression(node.identifier, null);
//    }
//
//    private void analyzeOutputStatement(ASTNodes.Output node) throws VariableNotDeclaredException, InHasNoValueException, TypeMismatchException, ScopeNotFoundException, InvalidTypesForBinaryOperatorException, FunctionDoesntExistException, InvalidArrayCallException {
//        analyzeExpression(node.node, null);
//    }
//
//    private void analyzeReturnStatement(ASTNodes.Return node, VarType type) throws InvalidReturnTypeException {
//        if (type == null) {
//            type = new VarType(VarType.PrimitiveType.VOID, false);
//        }
//        ASTNodes.ASTNode function = node.parent;
//        while (!(function instanceof ASTNodes.Function)) {
//            function = function.parent;
//        }
//        VarType functionReturnType = symbolTable.getFunctionReturnType(((ASTNodes.Function) function).name);
//        if (functionReturnType.type != type.type || functionReturnType.arrExt != type.arrExt) {
//            throw new InvalidReturnTypeException(functionReturnType, type);
//        }
//    }
//
//    private void analyzeEqStatement(ASTNodes.Eq node, VarType type) throws VariableNotDeclaredException, ScopeNotFoundException, AttemptToChangeConstValueException, TypeMismatchException, InvalidArrayCallException, FunctionDoesntExistException, InHasNoValueException, InvalidTypesForBinaryOperatorException, CannotAssignValueToPointerException {
//         SymbolTable.VarOrConstInfo var = symbolTable.varLookup(node.scope, node.identifier, node.time);
//         ASTNodes.ArrayCallIdx callIdx = (ASTNodes.ArrayCallIdx) node.arrCall;
//         boolean hasExt = var.type.arrExt;
//         if (callIdx != null) {
//             analyzeExpression(callIdx.expression, null);
//             hasExt = false;
//         }
//         if (var instanceof SymbolTable.ConstInfo) {
//             throw new AttemptToChangeConstValueException(var.name);
//         }
//         if (var.type.type != type.type || hasExt != type.arrExt) {
//             throw new TypeMismatchException(var.type, type);
//         }
//    }
//
//    private VarType analyzeExpression(ASTNodes.ASTNode node, VarType inValue) throws TypeMismatchException, VariableNotDeclaredException, ScopeNotFoundException, InHasNoValueException, InvalidTypesForBinaryOperatorException, FunctionDoesntExistException, InvalidArrayCallException {
//        if (node instanceof ASTNodes.BinaryOperator) {
//            return analyzeBinaryOperator((ASTNodes.BinaryOperator) node, inValue);
//        } else if (node instanceof ASTNodes.UnaryOperator) {
//            return analyzeUnaryOperator((ASTNodes.UnaryOperator) node, inValue);
//        } else if (node instanceof ASTNodes.Identifier) {
//            SymbolTable.VarOrConstInfo info = symbolTable.varLookup(node.scope, ((ASTNodes.Identifier) node).value, node.time);
//            ((ASTNodes.Identifier) node).info = info;
//            return info.type;
//        } else if (node instanceof ASTNodes.ArrayCall) {
//            return analyzeArrayCall((ASTNodes.ArrayCall) node, inValue);
//        } else if (node instanceof ASTNodes.FunctionCall) {
//            return analyzeFunctionCall((ASTNodes.FunctionCall) node, inValue);
//        } else if (node instanceof ASTNodes.Constant) {
//            return primitiveToTypeConverter.apply(node);
//        } else {
//            if (inValue == null) {
//                throw new InHasNoValueException();
//            }
//            return inValue;
//        }
//    }
//
//    private VarType analyzeArrayCall(ASTNodes.ArrayCall node, VarType inValue) throws VariableNotDeclaredException, ScopeNotFoundException, FunctionDoesntExistException, InHasNoValueException, TypeMismatchException, InvalidTypesForBinaryOperatorException, InvalidArrayCallException {
//        SymbolTable.VarOrConstInfo info = symbolTable.varLookup(node.scope, node.identifier, node.time);
//
//        ASTNodes.ArrayCallIdx idx = (ASTNodes.ArrayCallIdx) node.callIdx;
//        analyzeExpression(idx.expression, inValue);
//
//        node.returnResultType = new VarType(info.type.type, false);
//        return node.returnResultType;
//    }
//
//    private VarType analyzeFunctionCall(ASTNodes.FunctionCall node, VarType inValue) throws VariableNotDeclaredException, InHasNoValueException, TypeMismatchException, InvalidTypesForBinaryOperatorException, ScopeNotFoundException, FunctionDoesntExistException, InvalidArrayCallException {
//        List<VarType> paramTypes = new ArrayList<>();
//        ASTNodes.FunctionCallIdx idx = (ASTNodes.FunctionCallIdx) node.firstChild;
//        while (idx != null) {
//            paramTypes.add(analyzeExpression(idx.expression, inValue));
//            idx = (ASTNodes.FunctionCallIdx) idx.next;
//        }
//        SymbolTable.FunInfo funInfo = symbolTable.funLookup(node.identifier, paramTypes);
//        return funInfo.returnType;
//    }
//
//    private VarType analyzeUnaryOperator(ASTNodes.UnaryOperator node, VarType inValue) throws VariableNotDeclaredException, InHasNoValueException, TypeMismatchException, InvalidTypesForBinaryOperatorException, ScopeNotFoundException, FunctionDoesntExistException, InvalidArrayCallException {
//        VarType type = analyzeExpression(node.left, inValue);
//        if (checkPrimitiveTypesMatch(type, VarType.PrimitiveType.BOOL)) {
//            throw new TypeMismatchException(new VarType(VarType.PrimitiveType.BOOL, false), type);
//        }
//        return type;
//    }
//
//    private VarType analyzeBinaryOperator(ASTNodes.BinaryOperator node, VarType inValue) throws VariableNotDeclaredException, InHasNoValueException, TypeMismatchException, ScopeNotFoundException, InvalidTypesForBinaryOperatorException, FunctionDoesntExistException, InvalidArrayCallException {
//        VarType left = analyzeExpression(node.left, inValue);
//        VarType right = analyzeExpression(node.right, inValue);
//        Matcher arithmeticSign = Pattern.compile("[+\\-*/]").matcher(node.value);
//        Matcher moduloSign = Pattern.compile("%").matcher(node.value);
//        Matcher comparatorSign = Pattern.compile("(<=|<|>=|>|==|!=)").matcher(node.value);
//        if (arithmeticSign.matches()) {
//            if (checkPrimitiveTypesMatch(left, VarType.PrimitiveType.INT) && checkPrimitiveTypesMatch(right, VarType.PrimitiveType.INT)) {
//                return new VarType(VarType.PrimitiveType.INT, false);
//            } else if (checkPrimitiveTypesMatch(left, VarType.PrimitiveType.INT) && checkPrimitiveTypesMatch(right, VarType.PrimitiveType.FLOAT)) {
//                return new VarType(VarType.PrimitiveType.FLOAT, false);
//            } else if (checkPrimitiveTypesMatch(left, VarType.PrimitiveType.FLOAT) && checkPrimitiveTypesMatch(right, VarType.PrimitiveType.INT)) {
//                return new VarType(VarType.PrimitiveType.FLOAT, false);
//            } else if (checkPrimitiveTypesMatch(left, VarType.PrimitiveType.FLOAT) && checkPrimitiveTypesMatch(right, VarType.PrimitiveType.FLOAT)) {
//                return new VarType(VarType.PrimitiveType.FLOAT, false);
//            } else {
//                throw new InvalidTypesForBinaryOperatorException(node.value, left, right);
//            }
//        } else if (moduloSign.matches()) {
//            if (!checkPrimitiveTypesMatch(left, VarType.PrimitiveType.INT) || !checkPrimitiveTypesMatch(right, VarType.PrimitiveType.INT)) {
//                throw new InvalidTypesForBinaryOperatorException(node.value, left, right);
//            }
//            return new VarType(VarType.PrimitiveType.INT, false);
//        } else if (comparatorSign.matches()) {
//            if (checkPrimitiveTypesMatch(left, VarType.PrimitiveType.INT) && checkPrimitiveTypesMatch(right, VarType.PrimitiveType.INT)) {
//                return new VarType(VarType.PrimitiveType.BOOL, false);
//            } else if (checkPrimitiveTypesMatch(left, VarType.PrimitiveType.INT) && checkPrimitiveTypesMatch(right, VarType.PrimitiveType.FLOAT)) {
//                return new VarType(VarType.PrimitiveType.BOOL, false);
//            } else if (checkPrimitiveTypesMatch(left, VarType.PrimitiveType.FLOAT) && checkPrimitiveTypesMatch(right, VarType.PrimitiveType.INT)) {
//                return new VarType(VarType.PrimitiveType.BOOL, false);
//            } else if (checkPrimitiveTypesMatch(left, VarType.PrimitiveType.FLOAT) && checkPrimitiveTypesMatch(right, VarType.PrimitiveType.FLOAT)) {
//                return new VarType(VarType.PrimitiveType.BOOL, false);
//            } else {
//                throw new InvalidTypesForBinaryOperatorException(node.value, left, right);
//            }
//        } else {
//            if (checkPrimitiveTypesMatch(left, VarType.PrimitiveType.BOOL) && checkPrimitiveTypesMatch(right, VarType.PrimitiveType.BOOL)) {
//                return new VarType(VarType.PrimitiveType.BOOL, false);
//            } else {
//                throw new InvalidTypesForBinaryOperatorException(node.value, left, right);
//            }
//        }
//    }
//
//    private boolean checkPrimitiveTypesMatch(VarType type, VarType.PrimitiveType primitiveType) {
//        return type.type.equals(primitiveType) && !type.arrExt;
//    }
//
//    private void createFunctionTree(ASTNodes.ASTNode node, FunctionNode curr) {
//        if (node == null) {
//            return;
//        }
//        FunctionNode next = curr;
//        if (node instanceof ASTNodes.If) {
//            next = new FunctionNode();
//            curr.firstChild = next;
//            next.parent = curr;
//        } else if (node instanceof ASTNodes.Elif) {
//            next = new FunctionNode();
//            curr.neighbor = next;
//            next.parent = curr.parent;
//        } else if (node instanceof ASTNodes.Else) {
//            next = new FunctionNode();
//            curr.neighbor = next;
//            next.parent = curr.parent;
//            next.isElse = true;
//        } else if (node instanceof ASTNodes.Return) {
//            curr.returns = true;
//        }
//
//        for (ASTNodes.ASTNode child : node.getChildren()) {
//            createFunctionTree(child, next);
//        }
//    }
//
//    private boolean checkFunctionTree(FunctionNode node) {
//        if (node == null) {
//            return false;
//        }
//        if (node.returns) {
//            return true;
//        }
//        boolean hasElseFlag = false;
//        FunctionNode curr = node.firstChild;
//        while (curr != null) {
//            if (curr.isElse) {
//                hasElseFlag = true;
//            }
//            if (!curr.returns && !checkFunctionTree(curr)) {
//                return false;
//            }
//            curr = curr.neighbor;
//        }
//        return hasElseFlag;
//    }
//
//    private void checkBreakContinueInWhile(ASTNodes.ASTNode node, boolean inWhile) throws BreakNotInLoopException, ContinueNotInLoopException {
//        if (node == null) {
//            return;
//        }
//        boolean nowInWhile = inWhile;
//        if (node instanceof ASTNodes.While) {
//            nowInWhile = true;
//        }
//        if (node instanceof ASTNodes.Break && !inWhile) {
//            throw new BreakNotInLoopException();
//        }
//        if (node instanceof ASTNodes.Continue && !inWhile) {
//            throw new ContinueNotInLoopException();
//        }
//
//        for (ASTNodes.ASTNode child : node.getChildren()) {
//            checkBreakContinueInWhile(child, nowInWhile);
//        }
//    }
//
//    private void checkAllFunctionsReturn(ASTNodes.Program node) throws NotAllPathsHaveAReturnStatementException {
//        ASTNodes.FunctionList curr = (ASTNodes.FunctionList) node.firstFunction;
//        while (curr != null) {
//            if (curr.function.returnResultType.type != VarType.PrimitiveType.VOID) {
//                FunctionNode functionNode = new FunctionNode();
//                createFunctionTree(curr.function, functionNode);
//                if (!checkFunctionTree(functionNode)) {
//                    throw new NotAllPathsHaveAReturnStatementException(((ASTNodes.Function) curr.function).name);
//                }
//            }
//            curr = (ASTNodes.FunctionList) curr.nextFunction;
//        }
//    }
//
//
//
//    public SymbolTable analyze(AbstractSyntaxTree tree) throws ScopeNotFoundException, ConstantWithSameNameExistsException, AttemptToChangeConstValueException, VariableNotDeclaredException, VariableAlreadyDeclaredException, FunctionDefinedMultipleTimesException, TypeMismatchException, InHasNoValueException, InvalidTypesForBinaryOperatorException, FunctionDoesntExistException, InvalidArrayCallException, AttemptToFreeAPrimitiveValueException, InvalidReturnTypeException, NotAllPathsHaveAReturnStatementException, BreakNotInLoopException, ContinueNotInLoopException, CannotAssignValueToPointerException {
//        addScopeAndTimeToNodes(tree.root, 0, 0);
//        analyzeProgram((ASTNodes.Program) tree.root);
//        checkAllFunctionsReturn((ASTNodes.Program) tree.root);
//        checkBreakContinueInWhile(tree.root, false);
//        return symbolTable;
//    }
//}
