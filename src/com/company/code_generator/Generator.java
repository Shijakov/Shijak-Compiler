//package com.company.code_generator;
//
//import com.company.dev_exceptions.ErrorInGenerationException;
//import com.company.dev_exceptions.ScopeNotFoundException;
//import com.company.model.Pair;
//import com.company.parser.abstract_syntax_tree.ASTNodes;
//import com.company.parser.abstract_syntax_tree.AbstractSyntaxTree;
//import com.company.symbol_table.SymbolTable;
//import com.company.symbol_table.VarType;
//
//import java.util.*;
//import java.util.function.Supplier;
//
//public class Generator {
//
//    private static Map<String, Supplier<Runtime>> functionActivationRecords;
//
//    private static void generateForProgram(ASTNodes.Program node, StringBuilder sb, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        generateForDefinition((ASTNodes.Definition) node.definition, sb, shared, heap);
//        generateForFloatConstants(node, sb, shared, heap);
//
//        CommandRunner.runCommand(sb, ".text:", List.of());
//
//        heap.initialize(sb, shared);
//
//        CommandRunner.runCommand(sb, "j", List.of("main"));
//
//        heap.initializeProcedures(sb, shared);
//
//        generateForFunctionList((ASTNodes.FunctionList) node.firstFunction, sb, shared, heap);
//        sb.append("$END:\n").append("li $v0, 10\n").append("syscall");
//    }
//
//    private static void generateForFloatConstants(ASTNodes.ASTNode node, StringBuilder sb, SharedRuntime shared, Heap heap) {
//        if (node == null) {
//            return;
//        }
//        if (node instanceof ASTNodes.Definition) {
//            return;
//        }
//
//        if (node instanceof ASTNodes.FloatConstant) {
//            String floatName = String.format("$%s", ((ASTNodes.FloatConstant) node).value);
//            if (!shared.usedFloatConsts.contains(floatName)) {
//                sb.append(floatName).append(": .float ").append(floatName.substring(1)).append("\n");
//                shared.usedFloatConsts.add(floatName);
//            }
//        }
//
//        for(ASTNodes.ASTNode child : node.getChildren()) {
//            generateForFloatConstants(child, sb, shared, heap);
//        }
//    }
//
//    private static void generateForDefinition(ASTNodes.Definition node, StringBuilder sb, SharedRuntime shared, Heap heap) {
//        sb.append(".data:\n");
//        CommandRunner.runCommand(sb, String.format("%s: .word", shared.heapStartLabel), List.of("1"));
//        CommandRunner.runCommand(sb, String.format("%s: .word", shared.heapEndLabel), List.of("1"));
//        if (node == null) {
//            return;
//        }
//        generateForDefinitionInstance((ASTNodes.DefinitionInstance) node.firstDefinition, sb, shared, heap);
//    }
//
//    private static void generateForDefinitionInstance(ASTNodes.DefinitionInstance node, StringBuilder sb, SharedRuntime shared, Heap heap) {
//        if (node == null) {
//            return;
//        }
//        if (node.primitiveConstant instanceof ASTNodes.FloatConstant) {
//            sb.append(node.identifier).append(": .float ").append(((ASTNodes.FloatConstant) node.primitiveConstant).value).append("\n");
//        }
//        generateForDefinitionInstance((ASTNodes.DefinitionInstance) node.nextDefinition, sb, shared, heap);
//    }
//
//    private static void generateForFunctionList(ASTNodes.FunctionList node, StringBuilder sb, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        if (node == null) {
//            return;
//        }
//        generateForFunction((ASTNodes.Function) node.function, sb, shared, heap);
//        generateForFunctionList((ASTNodes.FunctionList) node.nextFunction, sb, shared, heap);
//    }
//
//    private static void generateForFunction(ASTNodes.Function node, StringBuilder sb, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        CommandRunner.runCommand(sb, "#==================================" + node.name + "================================", List.of());
//        CommandRunner.runCommand(sb, node.name + ":", List.of());
//
//        Runtime funRuntime = functionActivationRecords.get(node.name).get();
//
//        funRuntime.bootstrap(sb);
//
//        generateForStatement((ASTNodes.Statement) node.firstStatement, sb, funRuntime, shared, heap);
//
//        funRuntime.eraseUsedSpace(sb);
//
//        if (node.name.equals("main")) {
//            sb.append("j $END\n");
//        } else {
//            sb.append("jr $ra\n");
//        }
//    }
//
//    private static void generateForStatement(ASTNodes.Statement node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        if (node == null) {
//            return;
//        }
//        if (node.statement instanceof ASTNodes.ExpressionList) {
//            generateForExpressionList((ASTNodes.ExpressionList) node.statement, sb, runtime, shared, heap);
//        } else if (node.statement instanceof ASTNodes.If) {
//            generateForIfStatement((ASTNodes.If) node.statement, sb, runtime, shared, heap);
//        } else if (node.statement instanceof ASTNodes.Output) {
//            generateForOutputStatement((ASTNodes.Output) node.statement, sb, runtime, shared, heap);
//        } else if (node.statement instanceof ASTNodes.While) {
//            generateForWhileStatement((ASTNodes.While) node.statement, sb, runtime, shared, heap);
//        } else if (node.statement instanceof ASTNodes.Break) {
//            String endWhile = shared.peekLastEndWhile();
//            CommandRunner.runCommand(sb, "j", List.of(endWhile));
//        } else if (node.statement instanceof ASTNodes.Continue) {
//            String whileName = shared.peekLastWhile();
//            CommandRunner.runCommand(sb, "j", List.of(whileName));
//        } else if (node.statement instanceof ASTNodes.Input) {
//            generateForInputStatement((ASTNodes.Input) node.statement, sb, runtime, shared, heap);
//        } else if (node.statement instanceof ASTNodes.AllocArr) {
//            generateForAllocStatement((ASTNodes.AllocArr) node.statement, sb, runtime, shared, heap);
//        } else if (node.statement instanceof ASTNodes.FreeArr) {
//            generateForFreeStatement((ASTNodes.FreeArr) node.statement, sb, runtime, shared, heap);
//        } else if (node.statement instanceof ASTNodes.Return) {
//            generateForReturnCloser((ASTNodes.Return) node.statement, sb, runtime, shared, heap);
//        }
//        generateForStatement((ASTNodes.Statement) node.nextStatement, sb, runtime, shared, heap);
//    }
//
//    private static void generateForAllocStatement(ASTNodes.AllocArr node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        ASTNodes.ArrayCallIdx callIdx = (ASTNodes.ArrayCallIdx) ((ASTNodes.InitType) node.allocArrType).arrExt;
//        Pair<Integer, SyscallType> varInMem = runtime.getVariable(node.identifier);
//        generateForExpression(callIdx.expression, sb, runtime, shared, heap);
//        CommandRunner.runCommand(sb, "mul", List.of(shared.wordAcc, shared.wordAcc, "4"));
//        CommandRunner.runCommand(sb, "mflo", List.of(shared.heapMemSizeRequestRegister));
//        heap.alloc(sb);
//        CommandRunner.runCommand(sb, "sw", List.of(shared.heapMemReturnRegister, String.format("%d($sp)", varInMem.first)));
//    }
//
//    private static void generateForFreeStatement(ASTNodes.FreeArr node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        Pair<Integer, SyscallType> varInMem = runtime.getVariable(node.identifier);
//        CommandRunner.runCommand(sb, "lw", List.of(shared.heapMemSizeRequestRegister, String.format("%d($sp)", varInMem.first)));
//        heap.free(sb);
//        CommandRunner.runCommand(sb, "sw", List.of("$zero", String.format("%d($sp)", varInMem.first)));
//    }
//
//    private static void generateForInputStatement(ASTNodes.Input node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        if (node.identifier instanceof ASTNodes.Identifier) {
//            Pair<Integer, SyscallType> varInMem = runtime.getVariable(((ASTNodes.Identifier) node.identifier).value);
//            if (varInMem.second == SyscallType.FLOAT) {
//                CommandRunner.runCommand(sb, "li", List.of(shared.syscallRegister, shared.inputFloat));
//                CommandRunner.runCommand(sb, "syscall", List.of());
//                CommandRunner.runCommand(sb, "s.s", List.of(shared.floatInputResult, String.format("%d($sp)", varInMem.first)));
//            } else if (varInMem.second == SyscallType.INTEGER) {
//                CommandRunner.runCommand(sb, "li", List.of(shared.syscallRegister, shared.inputInteger));
//                CommandRunner.runCommand(sb, "syscall", List.of());
//                CommandRunner.runCommand(sb, "sw", List.of(shared.wordInputResult, String.format("%d($sp)", varInMem.first)));
//            } else {
//                CommandRunner.runCommand(sb, "li", List.of(shared.syscallRegister, shared.inputChar));
//                CommandRunner.runCommand(sb, "syscall", List.of());
//                CommandRunner.runCommand(sb, "sw", List.of(shared.wordInputResult, String.format("%d($sp)", varInMem.first)));
//            }
//        } else {
//            ASTNodes.ArrayCall arrCall = (ASTNodes.ArrayCall) node.identifier;
//            Pair<Integer, SyscallType> var = runtime.getVariable(arrCall.identifier);
//            ASTNodes.ArrayCallIdx callIdx = (ASTNodes.ArrayCallIdx) arrCall.callIdx;
//
//            generateForExpression(callIdx.expression, sb, runtime, shared, heap);
//            CommandRunner.runCommand(sb, "mul", List.of(shared.wordAcc, shared.wordAcc, "4"));
//            CommandRunner.runCommand(sb, "mflo", List.of(shared.wordAcc2));
//            CommandRunner.runCommand(sb, "lw", List.of(shared.wordAcc, String.format("%d($sp)", var.first)));
//            CommandRunner.runCommand(sb, "add", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
//
//            if (var.second == SyscallType.FLOAT_POINTER) {
//                CommandRunner.runCommand(sb, "li", List.of(shared.syscallRegister, shared.inputFloat));
//                CommandRunner.runCommand(sb, "syscall", List.of());
//                CommandRunner.runCommand(sb, "s.s", List.of(shared.floatInputResult, String.format("0(%s)", shared.wordAcc)));
//            } else if (var.second == SyscallType.INTEGER_POINTER) {
//                CommandRunner.runCommand(sb, "li", List.of(shared.syscallRegister, shared.inputInteger));
//                CommandRunner.runCommand(sb, "syscall", List.of());
//                CommandRunner.runCommand(sb, "sw", List.of(shared.wordInputResult, String.format("0(%s)", shared.wordAcc)));
//            } else {
//                CommandRunner.runCommand(sb, "li", List.of(shared.syscallRegister, shared.inputChar));
//                CommandRunner.runCommand(sb, "syscall", List.of());
//                CommandRunner.runCommand(sb, "sw", List.of(shared.wordInputResult, String.format("0(%s)", shared.wordAcc)));
//            }
//
//        }
//    }
//
//    private static void generateForOutputStatement(ASTNodes.Output node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        generateForExpression(node.node, sb, runtime, shared, heap);
//        if (runtime.accSyscallType == SyscallType.INTEGER) {
//            CommandRunner.runCommand(sb, "move", List.of(shared.integerToPrint, shared.wordAcc));
//            CommandRunner.runCommand(sb, "li", List.of(shared.syscallRegister, shared.printInteger));
//        } else if (runtime.accSyscallType == SyscallType.FLOAT) {
//            CommandRunner.runCommand(sb, "mov.s", List.of(shared.floatToPrint, shared.floatAcc));
//            CommandRunner.runCommand(sb, "li", List.of(shared.syscallRegister, shared.printFloat));
//        } else if (runtime.accSyscallType == SyscallType.CHAR) {
//            CommandRunner.runCommand(sb, "move", List.of(shared.charToPrint, shared.wordAcc));
//            CommandRunner.runCommand(sb, "li", List.of(shared.syscallRegister, shared.printChar));
//        } else {
//            throw new ErrorInGenerationException();
//        }
//        CommandRunner.runCommand(sb, "syscall", List.of());
//    }
//
//    private static void generateForElifStatement(ASTNodes.ASTNode node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap, int outerIf, int innerIf) throws ErrorInGenerationException {
//        CommandRunner.runCommand(sb, String.format("$%delif%d:", outerIf, innerIf), List.of());
//        if (node == null) {
//            return;
//        }
//
//        if (node instanceof ASTNodes.Elif) {
//            generateForExpression(((ASTNodes.Elif) node).condition, sb, runtime, shared, heap);
//            CommandRunner.runCommand(sb, "bne", List.of(shared.wordAcc, "1", String.format("$%delif%d", outerIf, innerIf + 1)));
//            generateForStatement((ASTNodes.Statement) ((ASTNodes.Elif) node).firstStatement, sb, runtime, shared, heap);
//            CommandRunner.runCommand(sb, "j", List.of(String.format("$%dendif", outerIf)));
//
//            generateForElifStatement(((ASTNodes.Elif) node).elif, sb, runtime, shared, heap, outerIf, innerIf + 1);
//        } else if (node instanceof ASTNodes.Else) {
//            generateForStatement((ASTNodes.Statement) ((ASTNodes.Else) node).firstStatement, sb, runtime, shared, heap);
//            CommandRunner.runCommand(sb, "j", List.of(String.format("$%dendif", outerIf)));
//        } else {
//            throw new ErrorInGenerationException();
//        }
//
//    }
//
//    private static void generateForIfStatement(ASTNodes.If node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        int outerIf = shared.ifCount;
//        int innerIf = 1;
//        shared.ifCount += 1;
//
//        generateForExpression(node.condition, sb, runtime, shared, heap);
//        CommandRunner.runCommand(sb, "bne", List.of(shared.wordAcc, "1", String.format("$%delif%d", outerIf, innerIf)));
//        generateForStatement((ASTNodes.Statement) node.firstStatement, sb, runtime, shared, heap);
//        CommandRunner.runCommand(sb, "j", List.of(String.format("$%dendif", outerIf)));
//
//        generateForElifStatement(node.elif, sb, runtime, shared, heap, outerIf, innerIf);
//
//        CommandRunner.runCommand(sb, String.format("$%dendif:", outerIf), List.of());
//    }
//
//    private static void generateForWhileStatement(ASTNodes.While node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        shared.pushToWhileStack();
//        String whileName = shared.peekLastWhile();
//        String endWhileName = shared.peekLastEndWhile();
//        CommandRunner.runCommand(sb, whileName + ":", List.of());
//        generateForExpression(node.condition, sb, runtime, shared, heap);
//        CommandRunner.runCommand(sb, "bne", List.of(shared.wordAcc, "1", endWhileName));
//        generateForStatement((ASTNodes.Statement) node.firstStatement, sb, runtime, shared, heap);
//        CommandRunner.runCommand(sb, "j", List.of(whileName));
//        CommandRunner.runCommand(sb, endWhileName + ":", List.of());
//        shared.popWhile();
//    }
//
//    private static void generateForExpressionList(ASTNodes.ExpressionList node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        if (node == null) {
//            return;
//        }
//        generateForExpressionList((ASTNodes.ExpressionList) node.nextExprList, sb, runtime, shared, heap);
//        generateForExpression(node.exprOrCloser, sb, runtime, shared, heap);
//        storeAccToIn(sb, runtime, shared, heap);
//    }
//
//    private static void generateForExpression(ASTNodes.ASTNode node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        if (node instanceof ASTNodes.BinaryOperator) {
//            generateForBinaryOperator((ASTNodes.BinaryOperator) node, sb, runtime, shared, heap);
//        } else if (node instanceof ASTNodes.UnaryOperator) {
//            generateForUnaryOperator((ASTNodes.UnaryOperator) node, sb, runtime, shared, heap);
//        } else if (node instanceof ASTNodes.Constant) {
//            generateForConstant((ASTNodes.Constant) node, sb, runtime, shared, heap);
//        } else if (node instanceof ASTNodes.InKeyword) {
//            generateForIn((ASTNodes.InKeyword) node, sb, runtime, shared, heap);
//        } else if (node instanceof ASTNodes.Identifier) {
//            generateForIdentifier((ASTNodes.Identifier) node, sb, runtime, shared, heap);
//        } else if (node instanceof ASTNodes.Eq) {
//            generateForEqCloser((ASTNodes.Eq) node, sb, runtime, shared, heap);
//        } else if (node instanceof ASTNodes.FunctionCall) {
//            generateForFunctionCall((ASTNodes.FunctionCall) node, sb, runtime, shared, heap);
//        } else if (node instanceof ASTNodes.Return) {
//            generateForReturnCloser((ASTNodes.Return) node, sb, runtime, shared, heap);
//        } else if (node instanceof ASTNodes.ArrayCall) {
//            generateForArrayCall((ASTNodes.ArrayCall) node, sb, runtime, shared, heap);
//        }
//    }
//
//    private static void generateForArrayCall(ASTNodes.ArrayCall node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        ASTNodes.ArrayCallIdx callIdx = (ASTNodes.ArrayCallIdx) node.callIdx;
//        generateForExpression(callIdx.expression, sb, runtime, shared, heap);
//        CommandRunner.runCommand(sb, "mul", List.of(shared.wordAcc, shared.wordAcc, "4"));
//        CommandRunner.runCommand(sb, "mflo", List.of(shared.wordAcc2));
//
//        Pair<Integer, SyscallType> type = runtime.getVariable(node.identifier);
//
//        CommandRunner.runCommand(sb, "lw", List.of(shared.wordAcc, String.format("%d($sp)", type.first)));
//        CommandRunner.runCommand(sb, "add", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
//
//        if (type.second == SyscallType.FLOAT_POINTER) {
//            CommandRunner.runCommand(sb, "l.s", List.of(shared.floatAcc, String.format("0(%s)", shared.wordAcc)));
//            runtime.accType = Coprocessor.FLOAT;
//            runtime.accSyscallType = SyscallType.FLOAT;
//        } else {
//            CommandRunner.runCommand(sb, "lw", List.of(shared.wordAcc, String.format("0(%s)", shared.wordAcc)));
//            runtime.accType = Coprocessor.WORD;
//            runtime.accSyscallType = type.second == SyscallType.INTEGER_POINTER ? SyscallType.INTEGER : SyscallType.CHAR;
//        }
//    }
//
//    private static void generateForReturnCloser(ASTNodes.Return node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) {
//        if (runtime.returnType == Coprocessor.WORD) {
//            CommandRunner.runCommand(sb, "move", List.of(shared.returnWordRegister, shared.inWordRegister));
//        } else if (runtime.returnType == Coprocessor.FLOAT){
//            CommandRunner.runCommand(sb, "mov.s", List.of(shared.returnFloatRegister, shared.inFloatRegister));
//        }
//        runtime.eraseUsedSpace(sb);
//        CommandRunner.runCommand(sb, "jr", List.of("$ra"));
//    }
//
//    private static void generateForEqCloser(ASTNodes.Eq node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        Pair<Integer, SyscallType> var = runtime.getVariable(node.identifier);
//        if (var.second == SyscallType.FLOAT) {
//            CommandRunner.runCommand(sb, "s.s", List.of(shared.inFloatRegister, String.format("%d($sp)", var.first)));
//        } else if (var.second == SyscallType.INTEGER || var.second == SyscallType.CHAR) {
//            CommandRunner.runCommand(sb, "sw", List.of(shared.inWordRegister, String.format("%d($sp)", var.first)));
//        } else {
//            if (node.arrCall == null) {
//                CommandRunner.runCommand(sb, "sw", List.of(shared.inWordRegister, String.format("%d($sp)", var.first)));
//            } else {
//                ASTNodes.ArrayCallIdx callIdx = (ASTNodes.ArrayCallIdx) node.arrCall;
//                generateForExpression(callIdx.expression, sb, runtime, shared, heap);
//                CommandRunner.runCommand(sb, "mul", List.of(shared.wordAcc, shared.wordAcc, "4"));
//                CommandRunner.runCommand(sb, "mflo", List.of(shared.wordAcc2));
//                CommandRunner.runCommand(sb, "lw", List.of(shared.wordAcc, String.format("%d($sp)", var.first)));
//                CommandRunner.runCommand(sb, "add", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
//                if (var.second == SyscallType.FLOAT_POINTER) {
//                    CommandRunner.runCommand(sb, "s.s", List.of(shared.inFloatRegister, String.format("0(%s)", shared.wordAcc)));
//                } else {
//                    CommandRunner.runCommand(sb, "sw", List.of(shared.inWordRegister, String.format("0(%s)", shared.wordAcc)));
//                }
//            }
//        }
//        runtime.accType = null;
//        runtime.accSyscallType = null;
//    }
//
//    private static void generateForBinaryOperator(ASTNodes.BinaryOperator node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        generateForExpression(node.left, sb, runtime, shared, heap);
//        storeAccToTmp(sb, runtime, shared, heap);
//        generateForExpression(node.right, sb, runtime, shared, heap);
//        getFromTmpToAcc2(sb, runtime, shared, heap);
//        convertAccumulatorsToSameType(sb, runtime, shared, heap);
//        switch (node.value) {
//            case "+":
//                if (runtime.accType == Coprocessor.WORD) {
//                    CommandRunner.runCommand(sb, "add", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
//                } else {
//                    CommandRunner.runCommand(sb, "add.s", List.of(shared.floatAcc, shared.floatAcc, shared.floatAcc2));
//                }
//                break;
//            case "-":
//                if (runtime.accType == Coprocessor.WORD) {
//                    CommandRunner.runCommand(sb, "sub", List.of(shared.wordAcc, shared.wordAcc2, shared.wordAcc));
//                } else {
//                    CommandRunner.runCommand(sb, "sub.s", List.of(shared.floatAcc, shared.floatAcc2, shared.floatAcc));
//                }
//                break;
//            case "*":
//                if (runtime.accType == Coprocessor.WORD) {
//                    CommandRunner.runCommand(sb, "mul", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
//                    CommandRunner.runCommand(sb, "mflo", List.of(shared.wordAcc));
//                } else {
//                    CommandRunner.runCommand(sb, "mul.s", List.of(shared.floatAcc, shared.floatAcc, shared.floatAcc2));
//                }
//                break;
//            case "/":
//                if (runtime.accType == Coprocessor.WORD) {
//                    CommandRunner.runCommand(sb, "div", List.of(shared.wordAcc2, shared.wordAcc));
//                    CommandRunner.runCommand(sb, "mflo", List.of(shared.wordAcc));
//                } else {
//                    CommandRunner.runCommand(sb, "div.s", List.of(shared.floatAcc, shared.floatAcc2, shared.floatAcc));
//                }
//                break;
//            case "%":
//                CommandRunner.runCommand(sb, "div", List.of(shared.wordAcc2, shared.wordAcc));
//                CommandRunner.runCommand(sb, "mfhi", List.of(shared.wordAcc));
//                break;
//            case "||":
//                CommandRunner.runCommand(sb, "or", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
//                break;
//            case "&&":
//                CommandRunner.runCommand(sb, "and", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
//                break;
//            case "==":
//                if (runtime.accType == Coprocessor.WORD) {
//                    CommandRunner.runCommand(sb, "seq", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
//                } else {
//                    CommandRunner.runCommand(sb, "c.eq.s", List.of(shared.floatAcc, shared.floatAcc2));
//                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc2, "1"));
//                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, "0"));
//                    CommandRunner.runCommand(sb, "movt", List.of(shared.wordAcc, shared.wordAcc2));
//                    runtime.accType = Coprocessor.WORD;
//                    runtime.accSyscallType = SyscallType.INTEGER;
//                }
//                break;
//            case "!=":
//                if (runtime.accType == Coprocessor.WORD) {
//                    CommandRunner.runCommand(sb, "sne", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
//                } else {
//                    CommandRunner.runCommand(sb, "c.eq.s", List.of(shared.floatAcc, shared.floatAcc2));
//                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc2, "1"));
//                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, "0"));
//                    CommandRunner.runCommand(sb, "movf", List.of(shared.wordAcc, shared.wordAcc2));
//                    runtime.accType = Coprocessor.WORD;
//                    runtime.accSyscallType = SyscallType.INTEGER;
//                }
//                break;
//            case "<":
//                if (runtime.accType == Coprocessor.WORD) {
//                    CommandRunner.runCommand(sb, "slt", List.of(shared.wordAcc, shared.wordAcc2, shared.wordAcc));
//                } else {
//                    CommandRunner.runCommand(sb, "c.lt.s", List.of(shared.floatAcc2, shared.floatAcc));
//                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc2, "1"));
//                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, "0"));
//                    CommandRunner.runCommand(sb, "movt", List.of(shared.wordAcc, shared.wordAcc2));
//                    runtime.accType = Coprocessor.WORD;
//                    runtime.accSyscallType = SyscallType.INTEGER;
//                }
//                break;
//            case "<=":
//                if (runtime.accType == Coprocessor.WORD) {
//                    CommandRunner.runCommand(sb, "sle", List.of(shared.wordAcc, shared.wordAcc2, shared.wordAcc));
//                } else {
//                    CommandRunner.runCommand(sb, "c.le.s", List.of(shared.floatAcc2, shared.floatAcc));
//                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc2, "1"));
//                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, "0"));
//                    CommandRunner.runCommand(sb, "movt", List.of(shared.wordAcc, shared.wordAcc2));
//                    runtime.accType = Coprocessor.WORD;
//                    runtime.accSyscallType = SyscallType.INTEGER;
//                }
//                break;
//            case ">":
//                if (runtime.accType == Coprocessor.WORD) {
//                    CommandRunner.runCommand(sb, "slt", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
//                } else {
//                    CommandRunner.runCommand(sb, "c.lt.s", List.of(shared.floatAcc, shared.floatAcc2));
//                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc2, "1"));
//                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, "0"));
//                    CommandRunner.runCommand(sb, "movt", List.of(shared.wordAcc, shared.wordAcc2));
//                    runtime.accType = Coprocessor.WORD;
//                    runtime.accSyscallType = SyscallType.INTEGER;
//                }
//                break;
//            case ">=":
//                if (runtime.accType == Coprocessor.WORD) {
//                    CommandRunner.runCommand(sb, "sle", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
//                } else {
//                    CommandRunner.runCommand(sb, "c.le.s", List.of(shared.floatAcc, shared.floatAcc2));
//                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc2, "1"));
//                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, "0"));
//                    CommandRunner.runCommand(sb, "movt", List.of(shared.wordAcc, shared.wordAcc2));
//                    runtime.accType = Coprocessor.WORD;
//                    runtime.accSyscallType = SyscallType.INTEGER;
//                }
//                break;
//        }
//    }
//
//    private static void generateForUnaryOperator(ASTNodes.UnaryOperator node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        if (node.value.equals("!")) {
//            generateForExpression(node.left, sb, runtime, shared, heap);
//            CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc2, "0"));
//            CommandRunner.runCommand(sb, "nor", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
//            runtime.accSyscallType = SyscallType.INTEGER;
//        }
//    }
//
//    private static void generateForConstant(ASTNodes.Constant node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) {
//        if (node instanceof ASTNodes.IntConstant) {
//            CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, node.value));
//            runtime.accType = Coprocessor.WORD;
//            runtime.accSyscallType = SyscallType.INTEGER;
//        } else if (node instanceof ASTNodes.CharConstant) {
//            int val = node.value.charAt(1);
//            CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, String.valueOf(val)));
//            runtime.accType = Coprocessor.WORD;
//            runtime.accSyscallType = SyscallType.CHAR;
//        } else if (node instanceof ASTNodes.BoolConstant) {
//            int val = node.value.equals("true") ? 1 : 0;
//            CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, String.valueOf(val)));
//            runtime.accType = Coprocessor.WORD;
//            runtime.accSyscallType = SyscallType.INTEGER;
//        } else {
//            CommandRunner.runCommand(sb, "l.s", List.of(shared.floatAcc, "$" + node.value));
//            runtime.accType = Coprocessor.FLOAT;
//            runtime.accSyscallType = SyscallType.FLOAT;
//        }
//    }
//
//    private static void generateForIn(ASTNodes.InKeyword node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) {
//        if (runtime.inType == Coprocessor.WORD) {
//            CommandRunner.runCommand(sb, "move", List.of(shared.wordAcc, shared.inWordRegister));
//            runtime.accType = Coprocessor.WORD;
//            runtime.accSyscallType = SyscallType.INTEGER;
//        } else {
//            CommandRunner.runCommand(sb, "mov.s", List.of(shared.floatAcc, shared.inFloatRegister));
//            runtime.accType = Coprocessor.FLOAT;
//            runtime.accSyscallType = SyscallType.FLOAT;
//        }
//    }
//
//    private static void generateForFunctionCall(ASTNodes.FunctionCall node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        ASTNodes.FunctionCallIdx callIdx = (ASTNodes.FunctionCallIdx) node.firstChild;
//        int numArgs = 0;
//        while (callIdx != null) {
//            generateForExpression(callIdx.expression, sb, runtime, shared, heap);
//            CommandRunner.runCommand(sb, "addi", List.of("$sp", "$sp", "-4"));
//            if (runtime.accType == Coprocessor.FLOAT) {
//                CommandRunner.runCommand(sb, "s.s", List.of(shared.floatAcc, "0($sp)"));
//            } else {
//                CommandRunner.runCommand(sb, "sw", List.of(shared.wordAcc, "0($sp)"));
//            }
//            runtime.stackPointerOffset += 4;
//            numArgs += 4;
//            callIdx = (ASTNodes.FunctionCallIdx) callIdx.next;
//        }
//
//        runtime.saveRegisters(sb);
//
//        CommandRunner.runCommand(sb, "jal", List.of(node.identifier));
//
//        runtime.stackPointerOffset -= numArgs;
//
//        if (shared.functionReturnTypes.get(node.identifier) == Coprocessor.FLOAT) {
//            CommandRunner.runCommand(sb, "mov.s", List.of(shared.floatAcc, shared.returnFloatRegister));
//        } else {
//            CommandRunner.runCommand(sb, "move", List.of(shared.wordAcc, shared.returnWordRegister));
//        }
//
//        runtime.restoreRegisters(sb);
//    }
//
//    private static void generateForIdentifier(ASTNodes.Identifier node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) {
//        if (node.info instanceof SymbolTable.ConstInfo) {
//            if (node.info.type.type == VarType.PrimitiveType.FLOAT) {
//                CommandRunner.runCommand(sb, "l.s", List.of(shared.floatAcc, node.info.name));
//                runtime.accType = Coprocessor.FLOAT;
//                runtime.accSyscallType = SyscallType.FLOAT;
//            } else if (node.info.type.type == VarType.PrimitiveType.INT) {
//                CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, node.info.type.value));
//                runtime.accType = Coprocessor.WORD;
//                runtime.accSyscallType = SyscallType.INTEGER;
//            } else if (node.info.type.type == VarType.PrimitiveType.CHAR) {
//                int val = node.info.type.value.charAt(1);
//                CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, String.valueOf(val)));
//                runtime.accType = Coprocessor.WORD;
//                runtime.accSyscallType = SyscallType.CHAR;
//            } else if (node.info.type.type == VarType.PrimitiveType.BOOL) {
//                int val = node.info.type.value.equals("true") ? 1 : 0;
//                CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, String.valueOf(val)));
//                runtime.accType = Coprocessor.WORD;
//                runtime.accSyscallType = SyscallType.INTEGER;
//            }
//        } else {
//            Pair<Integer, SyscallType> var = runtime.getVariable(node.value);
//            if (var.second == SyscallType.FLOAT) {
//                CommandRunner.runCommand(sb, "l.s", List.of(shared.floatAcc, String.format("%d($sp)", var.first)));
//                runtime.accType = Coprocessor.FLOAT;
//                runtime.accSyscallType = SyscallType.FLOAT;
//            } else if (var.second == SyscallType.INTEGER) {
//                CommandRunner.runCommand(sb, "lw", List.of(shared.wordAcc, String.format("%d($sp)", var.first)));
//                runtime.accType = Coprocessor.WORD;
//                runtime.accSyscallType = SyscallType.INTEGER;
//            } else if (var.second == SyscallType.CHAR) {
//                CommandRunner.runCommand(sb, "lw", List.of(shared.wordAcc, String.format("%d($sp)", var.first)));
//                runtime.accType = Coprocessor.WORD;
//                runtime.accSyscallType = SyscallType.CHAR;
//            } else {
//                CommandRunner.runCommand(sb, "lw", List.of(shared.wordAcc, String.format("%d($sp)", var.first)));
//                runtime.accType = Coprocessor.WORD;
//                if (var.second == SyscallType.FLOAT_POINTER) {
//                    runtime.accSyscallType = SyscallType.FLOAT_POINTER;
//                } else if (var.second == SyscallType.INTEGER_POINTER) {
//                    runtime.accSyscallType = SyscallType.INTEGER_POINTER;
//                } else {
//                    runtime.accSyscallType = SyscallType.CHAR_POINTER;
//                }
//            }
//        }
//    }
//
//    private static void storeAccToIn(StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) {
//        if (runtime.accType == Coprocessor.WORD) {
//            runtime.inType = Coprocessor.WORD;
//            sb.append("move ").append(shared.inWordRegister).append(", ").append(shared.wordAcc).append("\n");
//        } else if (runtime.accType == Coprocessor.FLOAT) {
//            runtime.inType = Coprocessor.FLOAT;
//            sb.append("mov.s ").append(shared.inFloatRegister).append(", ").append(shared.floatAcc).append("\n");
//        } else {
//            runtime.inType = null;
//        }
//    }
//
//    private static void storeAccToTmp(StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) {
//        if (runtime.accType == Coprocessor.WORD) {
//            String freeRegister = runtime.getNextFreeInt();
//            if (freeRegister == null) {
//                CommandRunner.runCommand(sb, "addi", List.of("$sp", "$sp", "-4"));
//                CommandRunner.runCommand(sb, "sw", List.of(shared.wordAcc, "0($sp)"));
//            } else {
//                CommandRunner.runCommand(sb, "move", List.of(freeRegister, shared.wordAcc));
//            }
//        } else if (runtime.accType == Coprocessor.FLOAT){
//            String freeRegister = runtime.getNextFreeFloat();
//            if (freeRegister == null) {
//                CommandRunner.runCommand(sb, "addi", List.of("$sp", "$sp", "-4"));
//                CommandRunner.runCommand(sb, "s.s", List.of(shared.floatAcc, "0($sp)"));
//            } else {
//                CommandRunner.runCommand(sb, "mov.s", List.of(freeRegister, shared.floatAcc));
//            }
//        }
//    }
//
//    private static void getFromTmpToAcc2(StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) throws ErrorInGenerationException {
//        String usedTmp = runtime.popUsedRegisters();
//        if (usedTmp.equals("SF")) {
//            CommandRunner.runCommand(sb, "l.s", List.of(shared.floatAcc2, "0($sp)"));
//            CommandRunner.runCommand(sb, "addi", List.of("$sp", "$sp", "4"));
//            runtime.acc2Type = Coprocessor.FLOAT;
//        } else if (usedTmp.equals("SI")) {
//            CommandRunner.runCommand(sb, "lw", List.of(shared.wordAcc2, "0($sp)"));
//            CommandRunner.runCommand(sb, "addi", List.of("$sp", "$sp", "4"));
//            runtime.acc2Type = Coprocessor.WORD;
//        } else if (usedTmp.startsWith("$f")) {
//            sb.append("mov.s ").append(shared.floatAcc2).append(", ").append(usedTmp).append("\n");
//            runtime.acc2Type = Coprocessor.FLOAT;
//        } else if (usedTmp.startsWith("$t")) {
//            sb.append("move ").append(shared.wordAcc2).append(", ").append(usedTmp).append("\n");
//            runtime.acc2Type = Coprocessor.WORD;
//        } else {
//            throw new ErrorInGenerationException();
//        }
//    }
//
//    private static void convertAccumulatorsToSameType(StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap) {
//        if (runtime.accType == Coprocessor.FLOAT || runtime.acc2Type == Coprocessor.FLOAT) {
//            if (runtime.accType == Coprocessor.WORD) {
//                CommandRunner.runCommand(sb, "mtc1", List.of(shared.wordAcc, shared.floatAcc));
//                CommandRunner.runCommand(sb, "cvt.s.w", List.of(shared.floatAcc, shared.floatAcc));
//                runtime.accType = Coprocessor.FLOAT;
//            } else if (runtime.acc2Type == Coprocessor.WORD) {
//                CommandRunner.runCommand(sb, "mtc1", List.of(shared.wordAcc2, shared.floatAcc2));
//                CommandRunner.runCommand(sb, "cvt.s.w", List.of(shared.floatAcc2, shared.floatAcc2));
//                runtime.acc2Type = Coprocessor.FLOAT;
//            }
//        }
//    }
//
//    private static void getLocalVariables(SymbolTable.Table table, List<Pair<String, SyscallType>> localVariables) {
//        if (table == null) {
//            return;
//        }
//
//        for (SymbolTable.VarInfo varInfo : table.row.values()) {
//            if (varInfo.isArgument) {
//                continue;
//            }
//
//            if (varInfo.type.type == VarType.PrimitiveType.FLOAT && !varInfo.type.arrExt) {
//                localVariables.add(new Pair<>(varInfo.name, SyscallType.FLOAT));
//            } else if (varInfo.type.type == VarType.PrimitiveType.CHAR && !varInfo.type.arrExt) {
//                localVariables.add(new Pair<>(varInfo.name, SyscallType.CHAR));
//            } else if (varInfo.type.type == VarType.PrimitiveType.INT && !varInfo.type.arrExt) {
//                localVariables.add(new Pair<>(varInfo.name, SyscallType.INTEGER));
//            } else if (varInfo.type.type == VarType.PrimitiveType.FLOAT) {
//                localVariables.add(new Pair<>(varInfo.name, SyscallType.FLOAT_POINTER));
//            } else if (varInfo.type.type == VarType.PrimitiveType.CHAR) {
//                localVariables.add(new Pair<>(varInfo.name, SyscallType.CHAR_POINTER));
//            } else if (varInfo.type.type == VarType.PrimitiveType.INT) {
//                localVariables.add(new Pair<>(varInfo.name, SyscallType.INTEGER_POINTER));
//            }
//        }
//
//        SymbolTable.Table child = (SymbolTable.Table) table.firstChild;
//        while (child != null) {
//            getLocalVariables(child, localVariables);
//            child = (SymbolTable.Table) child.neighbor;
//        }
//    }
//
//    private static void getFunctionParameters(List<SymbolTable.FunParam> funParams, List<Pair<String, SyscallType>> arguments) {
//        for (SymbolTable.FunParam param : funParams) {
//            if (param.type.type == VarType.PrimitiveType.FLOAT && !param.type.arrExt) {
//                arguments.add(new Pair<>(param.name, SyscallType.FLOAT));
//            } else if (param.type.type == VarType.PrimitiveType.CHAR && !param.type.arrExt) {
//                arguments.add(new Pair<>(param.name, SyscallType.CHAR));
//            } else if (param.type.type == VarType.PrimitiveType.INT && !param.type.arrExt) {
//                arguments.add(new Pair<>(param.name, SyscallType.INTEGER));
//            } else if (param.type.type == VarType.PrimitiveType.FLOAT) {
//                arguments.add(new Pair<>(param.name, SyscallType.FLOAT_POINTER));
//            } else if (param.type.type == VarType.PrimitiveType.CHAR) {
//                arguments.add(new Pair<>(param.name, SyscallType.CHAR_POINTER));
//            } else if (param.type.type == VarType.PrimitiveType.INT) {
//                arguments.add(new Pair<>(param.name, SyscallType.INTEGER_POINTER));
//            }
//        }
//    }
//
//    private static void createActivationRecords(SymbolTable symbolTable, SharedRuntime shared) throws ScopeNotFoundException {
//        functionActivationRecords = new HashMap<>();
//        for(SymbolTable.FunInfo funInfo : symbolTable.root.funRow.values()) {
//            List<Pair<String, SyscallType>> arguments = new ArrayList<>();
//            List<Pair<String, SyscallType>> localVariables = new ArrayList<>();
//
//            getFunctionParameters(funInfo.params, arguments);
//
//            SymbolTable.Table table = (SymbolTable.Table) symbolTable.findScope(funInfo.scopeId);
//            getLocalVariables(table, localVariables);
//
//            Coprocessor returnType = funInfo.returnType.type == VarType.PrimitiveType.FLOAT ? Coprocessor.FLOAT : Coprocessor.WORD;
//
//            functionActivationRecords.put(funInfo.funName, () -> new Runtime(arguments, localVariables, returnType));
//
//            shared.functionReturnTypes.put(funInfo.funName, returnType);
//        }
//    }
//
//    public static String generate(AbstractSyntaxTree tree, SymbolTable symbolTable) throws ErrorInGenerationException, ScopeNotFoundException {
//        StringBuilder result = new StringBuilder();
//        SharedRuntime shared = new SharedRuntime();
//        createActivationRecords(symbolTable, shared);
//        generateForProgram((ASTNodes.Program) tree.root, result, shared, new Heap());
//        return result.toString();
//    }
//}
