package com.company.code_generator;

import com.company.dev_exceptions.ErrorInGenerationException;
import com.company.dev_exceptions.GeneralDevException;
import com.company.parser.abstract_syntax_tree.ASTNodes;
import com.company.parser.abstract_syntax_tree.AbstractSyntaxTree;
import com.company.symbol_table.SymbolTable;
import com.company.symbol_table.variable_types.*;

import java.util.*;
import java.util.function.Supplier;

public class Generator {

    private final SymbolTable symbolTable;
    private final SharedRuntime shared;
    private final Heap heap;
    private final Map<String, Supplier<Runtime>> functionActivationRecords;

    public Generator(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.shared = new SharedRuntime();
        this.functionActivationRecords = new HashMap<>();
        this.heap = new Heap();
    }

    private void generateForProgram(ASTNodes.Program node, StringBuilder sb) throws Exception {
        generateForDefinition((ASTNodes.Definition) node.definition, sb);
        generateForFloatConstants(node, sb);

        CommandRunner.runCommand(sb, ".text:", List.of());

        heap.initialize(sb, shared);

        CommandRunner.runCommand(sb, "j", List.of("main"));

        heap.initializeProcedures(sb, shared);

        generateForConstructList((ASTNodes.ConstructList) node.constructList, sb);
        sb.append("$END:\n").append("li $v0, 10\n").append("syscall");
    }

    private void generateForFloatConstants(ASTNodes.ASTNode node, StringBuilder sb) {
        if (node == null) {
            return;
        }
        if (node instanceof ASTNodes.Definition) {
            return;
        }

        if (node instanceof ASTNodes.FloatConstant) {
            String floatName = String.format("$%s", ((ASTNodes.FloatConstant) node).value);
            if (!shared.usedFloatConsts.contains(floatName)) {
                sb.append(floatName).append(": .float ").append(floatName.substring(1)).append("\n");
                shared.usedFloatConsts.add(floatName);
            }
        }

        for(ASTNodes.ASTNode child : node.getChildren()) {
            generateForFloatConstants(child, sb);
        }
    }

    private void generateForDefinition(ASTNodes.Definition node, StringBuilder sb) {
        sb.append(".data:\n");
        CommandRunner.runCommand(sb, String.format("%s: .word", shared.heapStartLabel), List.of("1"));
        CommandRunner.runCommand(sb, String.format("%s: .word", shared.heapEndLabel), List.of("1"));
    }

    private void generateForConstructList(ASTNodes.ConstructList node, StringBuilder sb) throws Exception {
        if (node == null) {
            return;
        }
        if (node.construct instanceof ASTNodes.FunctionDefinition) {
            generateForFunction((ASTNodes.FunctionDefinition) node.construct, sb);
        }
        generateForConstructList((ASTNodes.ConstructList) node.nextConstruct, sb);
    }

    private void generateForFunction(ASTNodes.FunctionDefinition node, StringBuilder sb) throws Exception {
        CommandRunner.runCommand(sb, "#==================================" + node.functionName + "================================", List.of());
        CommandRunner.runCommand(sb, node.functionName + ":", List.of());

        Runtime funRuntime = functionActivationRecords.get(node.functionName).get();

        funRuntime.bootstrap(sb);

        generateForStatement((ASTNodes.Statement) node.firstStatement, sb, funRuntime);

        funRuntime.eraseUsedSpace(sb);

        if (node.functionName.equals("main")) {
            sb.append("j $END\n");
        } else {
            sb.append("jr $ra\n");
        }
    }

    private void generateForStatement(ASTNodes.Statement node, StringBuilder sb, Runtime runtime) throws Exception {
        if (node == null) {
            return;
        }
        if (node.statement instanceof ASTNodes.ExpressionList) {
            generateForExpressionList((ASTNodes.ExpressionList) node.statement, sb, runtime);
        } else if (node.statement instanceof ASTNodes.If) {
            generateForIfStatement((ASTNodes.If) node.statement, sb, runtime);
        } else if (node.statement instanceof ASTNodes.Output) {
            generateForOutputStatement((ASTNodes.Output) node.statement, sb, runtime);
        } else if (node.statement instanceof ASTNodes.While) {
            generateForWhileStatement((ASTNodes.While) node.statement, sb, runtime);
        } else if (node.statement instanceof ASTNodes.Break) {
            String endWhile = shared.peekLastEndWhile();
            CommandRunner.runCommand(sb, "j", List.of(endWhile));
        } else if (node.statement instanceof ASTNodes.Continue) {
            String whileName = shared.peekLastWhile();
            CommandRunner.runCommand(sb, "j", List.of(whileName));
        } else if (node.statement instanceof ASTNodes.Input) {
            generateForInputStatement((ASTNodes.Input) node.statement, sb, runtime);
        } else if (node.statement instanceof ASTNodes.AllocArr) {
            generateForAllocStatement((ASTNodes.AllocArr) node.statement, sb, runtime);
        } else if (node.statement instanceof ASTNodes.FreeInstance) {
            generateForFreeStatement((ASTNodes.FreeInstance) node.statement, sb, runtime);
        } else if (node.statement instanceof ASTNodes.Return) {
            generateForReturnCloser((ASTNodes.Return) node.statement, sb, runtime);
        } else if (node.statement instanceof ASTNodes.FillBag) {
            generateForFillBagStatement((ASTNodes.FillBag) node.statement, sb, runtime);
        }
        generateForStatement((ASTNodes.Statement) node.nextStatement, sb, runtime);
    }

    private void generateForFillBagStatement(ASTNodes.FillBag node, StringBuilder sb, Runtime runtime) throws Exception {
        var bagInfo = symbolTable.bagLookup(node.bagName);
        var memSize = bagInfo.getNumFields() * 4;

        CommandRunner.runCommand(sb, "li", List.of(shared.heapMemSizeRequestRegister, Integer.toString(memSize)));

        runtime.saveRegisters(sb);
        heap.alloc(sb);
        runtime.restoreRegisters(sb);

        generateForVariable((ASTNodes.Variable) node.assignableInstance, sb, runtime);

        CommandRunner.runCommand(sb, "sw", List.of(shared.heapMemReturnRegister, String.format("0(%s)", shared.wordAcc)));
    }

    private void generateForAllocStatement(ASTNodes.AllocArr node, StringBuilder sb, Runtime runtime) throws Exception {
        generateForVariable((ASTNodes.Variable) node.assignableInstance, sb, runtime);

        RuntimeHelper.storeAccToTmp(sb, runtime, shared);
        generateForExpression(node.expression, sb, runtime);
        RuntimeHelper.getFromTmpToAcc2(sb, runtime, shared);


        CommandRunner.runCommand(sb, "mul", List.of(shared.wordAcc, shared.wordAcc, "4"));
        CommandRunner.runCommand(sb, "mflo", List.of(shared.heapMemSizeRequestRegister));

        runtime.saveRegisters(sb);
        heap.alloc(sb);
        runtime.restoreRegisters(sb);

        CommandRunner.runCommand(sb, "sw", List.of(shared.heapMemReturnRegister, String.format("0(%s)", shared.wordAcc2)));
    }

    private void generateForFreeStatement(ASTNodes.FreeInstance node, StringBuilder sb, Runtime runtime) throws Exception {
        generateForVariable((ASTNodes.Variable) node.assignableInstance, sb, runtime);

        CommandRunner.runCommand(sb, "lw", List.of(shared.heapMemSizeRequestRegister, String.format("0(%s)", shared.wordAcc)));

        runtime.saveRegisters(sb);
        heap.free(sb);
        runtime.restoreRegisters(sb);

        CommandRunner.runCommand(sb, "sw", List.of("$zero", String.format("0(%s)", shared.wordAcc)));
    }

    private void generateForInputStatement(ASTNodes.Input node, StringBuilder sb, Runtime runtime) throws Exception {
        var varType = generateForVariable((ASTNodes.Variable) node.assignableInstance, sb, runtime);
        if (varType.equals(new FloatType())) {
            CommandRunner.runCommand(sb, "li", List.of(shared.syscallRegister, shared.inputFloat));
            CommandRunner.runCommand(sb, "syscall", List.of());
            CommandRunner.runCommand(sb, "s.s", List.of(shared.floatInputResult, String.format("0(%s)", shared.wordAcc)));
        } else if (varType.equals(new IntType())) {
            CommandRunner.runCommand(sb, "li", List.of(shared.syscallRegister, shared.inputInteger));
            CommandRunner.runCommand(sb, "syscall", List.of());
            CommandRunner.runCommand(sb, "sw", List.of(shared.wordInputResult, String.format("0(%s)", shared.wordAcc)));
        } else if (varType.equals(new CharType())) {
            CommandRunner.runCommand(sb, "li", List.of(shared.syscallRegister, shared.inputChar));
            CommandRunner.runCommand(sb, "syscall", List.of());
            CommandRunner.runCommand(sb, "sw", List.of(shared.wordInputResult, String.format("0(%s)", shared.wordAcc)));
        } else {
            throw new GeneralDevException("generateForInputStatement: generateForVariable returned unknown syscall type");
        }

        runtime.accType = null;
        runtime.accSyscallType = null;
    }

    private void generateForOutputStatement(ASTNodes.Output node, StringBuilder sb, Runtime runtime) throws Exception {
        generateForExpression(node.expression, sb, runtime);
        if (runtime.accSyscallType == SyscallType.INTEGER) {
            CommandRunner.runCommand(sb, "move", List.of(shared.integerToPrint, shared.wordAcc));
            CommandRunner.runCommand(sb, "li", List.of(shared.syscallRegister, shared.printInteger));
        } else if (runtime.accSyscallType == SyscallType.FLOAT) {
            CommandRunner.runCommand(sb, "mov.s", List.of(shared.floatToPrint, shared.floatAcc));
            CommandRunner.runCommand(sb, "li", List.of(shared.syscallRegister, shared.printFloat));
        } else if (runtime.accSyscallType == SyscallType.CHAR) {
            CommandRunner.runCommand(sb, "move", List.of(shared.charToPrint, shared.wordAcc));
            CommandRunner.runCommand(sb, "li", List.of(shared.syscallRegister, shared.printChar));
        } else {
            throw new ErrorInGenerationException("generateForOutputStatement received an unexpected type");
        }
        CommandRunner.runCommand(sb, "syscall", List.of());
    }

    private void generateForElifStatement(ASTNodes.ASTNode node, StringBuilder sb, Runtime runtime, SharedRuntime shared, Heap heap, int outerIf, int innerIf) throws Exception {
        CommandRunner.runCommand(sb, String.format("$%delif%d:", outerIf, innerIf), List.of());
        if (node == null) {
            return;
        }

        if (node instanceof ASTNodes.Elif) {
            generateForExpression(((ASTNodes.Elif) node).condition, sb, runtime);
            CommandRunner.runCommand(sb, "bne", List.of(shared.wordAcc, "1", String.format("$%delif%d", outerIf, innerIf + 1)));
            generateForStatement((ASTNodes.Statement) ((ASTNodes.Elif) node).firstStatement, sb, runtime);
            CommandRunner.runCommand(sb, "j", List.of(String.format("$%dendif", outerIf)));

            generateForElifStatement(((ASTNodes.Elif) node).elif, sb, runtime, shared, heap, outerIf, innerIf + 1);
        } else if (node instanceof ASTNodes.Else) {
            generateForStatement((ASTNodes.Statement) ((ASTNodes.Else) node).firstStatement, sb, runtime);
            CommandRunner.runCommand(sb, "j", List.of(String.format("$%dendif", outerIf)));
        } else {
            throw new ErrorInGenerationException("generateForElifStatement received an unexpected node");
        }

    }

    private void generateForIfStatement(ASTNodes.If node, StringBuilder sb, Runtime runtime) throws Exception {
        int outerIf = shared.ifCount;
        int innerIf = 1;
        shared.ifCount += 1;

        generateForExpression(node.condition, sb, runtime);
        CommandRunner.runCommand(sb, "bne", List.of(shared.wordAcc, "1", String.format("$%delif%d", outerIf, innerIf)));
        generateForStatement((ASTNodes.Statement) node.firstStatement, sb, runtime);
        CommandRunner.runCommand(sb, "j", List.of(String.format("$%dendif", outerIf)));

        generateForElifStatement(node.elif, sb, runtime, shared, heap, outerIf, innerIf);

        CommandRunner.runCommand(sb, String.format("$%dendif:", outerIf), List.of());
    }

    private void generateForWhileStatement(ASTNodes.While node, StringBuilder sb, Runtime runtime) throws Exception {
        shared.pushToWhileStack();
        String whileName = shared.peekLastWhile();
        String endWhileName = shared.peekLastEndWhile();
        CommandRunner.runCommand(sb, whileName + ":", List.of());
        generateForExpression(node.condition, sb, runtime);
        CommandRunner.runCommand(sb, "bne", List.of(shared.wordAcc, "1", endWhileName));
        generateForStatement((ASTNodes.Statement) node.firstStatement, sb, runtime);
        CommandRunner.runCommand(sb, "j", List.of(whileName));
        CommandRunner.runCommand(sb, endWhileName + ":", List.of());
        shared.popWhile();
    }

    private void generateForExpressionList(ASTNodes.ExpressionList node, StringBuilder sb, Runtime runtime) throws Exception {
        if (node == null) {
            return;
        }
        generateForExpressionList((ASTNodes.ExpressionList) node.nextExprList, sb, runtime);
        generateForExpression(node.exprOrCloser, sb, runtime);
        storeAccToIn(sb, runtime);
    }

    private void generateForExpression(ASTNodes.ASTNode node, StringBuilder sb, Runtime runtime) throws Exception {
        if (node instanceof ASTNodes.BinaryOperator) {
            generateForBinaryOperator((ASTNodes.BinaryOperator) node, sb, runtime);
        } else if (node instanceof ASTNodes.UnaryOperator) {
            generateForUnaryOperator((ASTNodes.UnaryOperator) node, sb, runtime);
        } else if (node instanceof ASTNodes.Constant) {
            generateForConstant((ASTNodes.Constant) node, sb, runtime);
        } else if (node instanceof ASTNodes.InKeyword) {
            generateForIn((ASTNodes.InKeyword) node, sb, runtime);
        } else if (node instanceof ASTNodes.Variable) {
            var type = generateForVariable((ASTNodes.Variable) node, sb, runtime);
            if (type.equals(new FloatType())) {
                CommandRunner.runCommand(sb, "l.s", List.of(shared.floatAcc, String.format("0(%s)", shared.wordAcc)));
                runtime.accSyscallType = SyscallType.FLOAT;
                runtime.accType = Coprocessor.FLOAT;
            }
            else if (type.equals(new CharType())) {
                CommandRunner.runCommand(sb, "lw", List.of(shared.wordAcc, String.format("0(%s)", shared.wordAcc)));
                runtime.accSyscallType = SyscallType.CHAR;
                runtime.accType = Coprocessor.WORD;
            } else {
                runtime.accSyscallType = SyscallType.INTEGER;
                CommandRunner.runCommand(sb, "lw", List.of(shared.wordAcc, String.format("0(%s)", shared.wordAcc)));
                runtime.accType = Coprocessor.WORD;
            }
        } else if (node instanceof ASTNodes.Eq) {
            generateForEqCloser((ASTNodes.Eq) node, sb, runtime);
        } else if (node instanceof ASTNodes.FunctionCall) {
            generateForFunctionCall((ASTNodes.FunctionCall) node, sb, runtime);
        } else if (node instanceof ASTNodes.Return) {
            generateForReturnCloser((ASTNodes.Return) node, sb, runtime);
        } else {
            throw new GeneralDevException("generateForExpression did not match any expression");
        }
    }

//    private void generateForArrayCall(ASTNodes.ArrayCall node, StringBuilder sb, Runtime runtime) throws Exception {
//        ASTNodes.ArrayCallIdx callIdx = (ASTNodes.ArrayCallIdx) node.callIdx;
//        generateForExpression(callIdx.expression, sb, runtime);
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

    private void generateForReturnCloser(ASTNodes.Return node, StringBuilder sb, Runtime runtime) {
        if (runtime.returnType == Coprocessor.WORD) {
            CommandRunner.runCommand(sb, "move", List.of(shared.returnWordRegister, shared.inWordRegister));
        } else if (runtime.returnType == Coprocessor.FLOAT){
            CommandRunner.runCommand(sb, "mov.s", List.of(shared.returnFloatRegister, shared.inFloatRegister));
        }
        runtime.eraseUsedSpace(sb);
        CommandRunner.runCommand(sb, "jr", List.of("$ra"));
    }

    private void generateForEqCloser(ASTNodes.Eq node, StringBuilder sb, Runtime runtime) throws Exception {
        generateForVariable((ASTNodes.Variable) node.assignableInstance, sb, runtime);

        if (runtime.accType == Coprocessor.WORD) {
            CommandRunner.runCommand(sb, "sw", List.of(shared.inWordRegister, String.format("0(%s)", shared.wordAcc)));
        } else {
            CommandRunner.runCommand(sb, "s.s", List.of(shared.inFloatRegister, String.format("0(%s)", shared.wordAcc)));
        }

        runtime.accType = null;
        runtime.accSyscallType = null;
    }

    private void generateForBinaryOperator(ASTNodes.BinaryOperator node, StringBuilder sb, Runtime runtime) throws Exception {
        generateForExpression(node.left, sb, runtime);
        RuntimeHelper.storeAccToTmp(sb, runtime, shared);
        generateForExpression(node.right, sb, runtime);
        RuntimeHelper.getFromTmpToAcc2(sb, runtime, shared);
        convertAccumulatorsToSameType(sb, runtime);
        switch (node.operator) {
            case "+":
                if (runtime.accType == Coprocessor.WORD) {
                    CommandRunner.runCommand(sb, "add", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
                } else {
                    CommandRunner.runCommand(sb, "add.s", List.of(shared.floatAcc, shared.floatAcc, shared.floatAcc2));
                }
                break;
            case "-":
                if (runtime.accType == Coprocessor.WORD) {
                    CommandRunner.runCommand(sb, "sub", List.of(shared.wordAcc, shared.wordAcc2, shared.wordAcc));
                } else {
                    CommandRunner.runCommand(sb, "sub.s", List.of(shared.floatAcc, shared.floatAcc2, shared.floatAcc));
                }
                break;
            case "*":
                if (runtime.accType == Coprocessor.WORD) {
                    CommandRunner.runCommand(sb, "mul", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
                    CommandRunner.runCommand(sb, "mflo", List.of(shared.wordAcc));
                } else {
                    CommandRunner.runCommand(sb, "mul.s", List.of(shared.floatAcc, shared.floatAcc, shared.floatAcc2));
                }
                break;
            case "/":
                if (runtime.accType == Coprocessor.WORD) {
                    CommandRunner.runCommand(sb, "div", List.of(shared.wordAcc2, shared.wordAcc));
                    CommandRunner.runCommand(sb, "mflo", List.of(shared.wordAcc));
                } else {
                    CommandRunner.runCommand(sb, "div.s", List.of(shared.floatAcc, shared.floatAcc2, shared.floatAcc));
                }
                break;
            case "%":
                CommandRunner.runCommand(sb, "div", List.of(shared.wordAcc2, shared.wordAcc));
                CommandRunner.runCommand(sb, "mfhi", List.of(shared.wordAcc));
                break;
            case "||":
                CommandRunner.runCommand(sb, "or", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
                break;
            case "&&":
                CommandRunner.runCommand(sb, "and", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
                break;
            case "==":
                if (runtime.accType == Coprocessor.WORD) {
                    CommandRunner.runCommand(sb, "seq", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
                } else {
                    CommandRunner.runCommand(sb, "c.eq.s", List.of(shared.floatAcc, shared.floatAcc2));
                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc2, "1"));
                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, "0"));
                    CommandRunner.runCommand(sb, "movt", List.of(shared.wordAcc, shared.wordAcc2));
                    runtime.accType = Coprocessor.WORD;
                    runtime.accSyscallType = SyscallType.INTEGER;
                }
                break;
            case "!=":
                if (runtime.accType == Coprocessor.WORD) {
                    CommandRunner.runCommand(sb, "sne", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
                } else {
                    CommandRunner.runCommand(sb, "c.eq.s", List.of(shared.floatAcc, shared.floatAcc2));
                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc2, "1"));
                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, "0"));
                    CommandRunner.runCommand(sb, "movf", List.of(shared.wordAcc, shared.wordAcc2));
                    runtime.accType = Coprocessor.WORD;
                    runtime.accSyscallType = SyscallType.INTEGER;
                }
                break;
            case "<":
                if (runtime.accType == Coprocessor.WORD) {
                    CommandRunner.runCommand(sb, "slt", List.of(shared.wordAcc, shared.wordAcc2, shared.wordAcc));
                } else {
                    CommandRunner.runCommand(sb, "c.lt.s", List.of(shared.floatAcc2, shared.floatAcc));
                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc2, "1"));
                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, "0"));
                    CommandRunner.runCommand(sb, "movt", List.of(shared.wordAcc, shared.wordAcc2));
                    runtime.accType = Coprocessor.WORD;
                    runtime.accSyscallType = SyscallType.INTEGER;
                }
                break;
            case "<=":
                if (runtime.accType == Coprocessor.WORD) {
                    CommandRunner.runCommand(sb, "sle", List.of(shared.wordAcc, shared.wordAcc2, shared.wordAcc));
                } else {
                    CommandRunner.runCommand(sb, "c.le.s", List.of(shared.floatAcc2, shared.floatAcc));
                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc2, "1"));
                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, "0"));
                    CommandRunner.runCommand(sb, "movt", List.of(shared.wordAcc, shared.wordAcc2));
                    runtime.accType = Coprocessor.WORD;
                    runtime.accSyscallType = SyscallType.INTEGER;
                }
                break;
            case ">":
                if (runtime.accType == Coprocessor.WORD) {
                    CommandRunner.runCommand(sb, "slt", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
                } else {
                    CommandRunner.runCommand(sb, "c.lt.s", List.of(shared.floatAcc, shared.floatAcc2));
                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc2, "1"));
                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, "0"));
                    CommandRunner.runCommand(sb, "movt", List.of(shared.wordAcc, shared.wordAcc2));
                    runtime.accType = Coprocessor.WORD;
                    runtime.accSyscallType = SyscallType.INTEGER;
                }
                break;
            case ">=":
                if (runtime.accType == Coprocessor.WORD) {
                    CommandRunner.runCommand(sb, "sle", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
                } else {
                    CommandRunner.runCommand(sb, "c.le.s", List.of(shared.floatAcc, shared.floatAcc2));
                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc2, "1"));
                    CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, "0"));
                    CommandRunner.runCommand(sb, "movt", List.of(shared.wordAcc, shared.wordAcc2));
                    runtime.accType = Coprocessor.WORD;
                    runtime.accSyscallType = SyscallType.INTEGER;
                }
                break;
            default:
                throw new GeneralDevException("generateForBinaryOperator did not match any operator");
        }
    }

    private void generateForUnaryOperator(ASTNodes.UnaryOperator node, StringBuilder sb, Runtime runtime) throws Exception {
        if (node.operator.equals("!")) {
            generateForExpression(node.left, sb, runtime);
            CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc2, "0"));
            CommandRunner.runCommand(sb, "nor", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));
            runtime.accSyscallType = SyscallType.INTEGER;
        }
    }

    private void generateForConstant(ASTNodes.Constant node, StringBuilder sb, Runtime runtime) {
        if (node instanceof ASTNodes.IntConstant) {
            CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, node.value));
            runtime.accType = Coprocessor.WORD;
            runtime.accSyscallType = SyscallType.INTEGER;
        } else if (node instanceof ASTNodes.CharConstant) {
            int val = node.value.charAt(1);
            CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, String.valueOf(val)));
            runtime.accType = Coprocessor.WORD;
            runtime.accSyscallType = SyscallType.CHAR;
        } else if (node instanceof ASTNodes.BoolConstant) {
            int val = node.value.equals("true") ? 1 : 0;
            CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc, String.valueOf(val)));
            runtime.accType = Coprocessor.WORD;
            runtime.accSyscallType = SyscallType.INTEGER;
        } else {
            CommandRunner.runCommand(sb, "l.s", List.of(shared.floatAcc, "$" + node.value));
            runtime.accType = Coprocessor.FLOAT;
            runtime.accSyscallType = SyscallType.FLOAT;
        }
    }

    private void generateForIn(ASTNodes.InKeyword node, StringBuilder sb, Runtime runtime) {
        if (runtime.inType == Coprocessor.WORD) {
            CommandRunner.runCommand(sb, "move", List.of(shared.wordAcc, shared.inWordRegister));
            runtime.accType = Coprocessor.WORD;
            runtime.accSyscallType = SyscallType.INTEGER;
        } else {
            CommandRunner.runCommand(sb, "mov.s", List.of(shared.floatAcc, shared.inFloatRegister));
            runtime.accType = Coprocessor.FLOAT;
            runtime.accSyscallType = SyscallType.FLOAT;
        }
    }

    private void generateForFunctionCall(ASTNodes.FunctionCall node, StringBuilder sb, Runtime runtime) throws Exception {
        var callArg = (ASTNodes.FunctionCallArgument) node.firstArgument;
        int numArgs = 0;

        while (callArg != null) {
            generateForExpression(callArg.expression, sb, runtime);
            CommandRunner.runCommand(sb, "addi", List.of("$sp", "$sp", "-4"));
            if (runtime.accType == Coprocessor.FLOAT) {
                CommandRunner.runCommand(sb, "s.s", List.of(shared.floatAcc, "0($sp)"));
            } else {
                CommandRunner.runCommand(sb, "sw", List.of(shared.wordAcc, "0($sp)"));
            }
            runtime.stackPointerOffset += 4;
            numArgs += 4;
            callArg = (ASTNodes.FunctionCallArgument) callArg.next;
        }

        runtime.saveRegisters(sb);

        CommandRunner.runCommand(sb, "jal", List.of(node.identifier));

        runtime.stackPointerOffset -= numArgs;

        var funInfo = symbolTable.funLookup(node.identifier);

        var coprocessorType = CoprocessorHelper.convertVarType(funInfo.returnType);

        if (coprocessorType.equals(Coprocessor.FLOAT)) {
            CommandRunner.runCommand(sb, "mov.s", List.of(shared.floatAcc, shared.returnFloatRegister));
        } else {
            CommandRunner.runCommand(sb, "move", List.of(shared.wordAcc, shared.returnWordRegister));
        }

        runtime.restoreRegisters(sb);
    }

    private void generateForDrilledInstance(VarType varType, StringBuilder sb, Runtime runtime) {
        if (varType.equals(new FloatType())) {
            CommandRunner.runCommand(sb, "l.s", List.of(shared.floatAcc, String.format("0(%s)", shared.wordAcc)));
            runtime.accType = Coprocessor.FLOAT;
            runtime.accSyscallType = SyscallType.FLOAT;
        } else if (varType.equals(new IntType()) || varType.equals(new BoolType())) {
            CommandRunner.runCommand(sb, "lw", List.of(shared.wordAcc, String.format("0(%s)", shared.wordAcc)));
            runtime.accType = Coprocessor.WORD;
            runtime.accSyscallType = SyscallType.INTEGER;
        } else if (varType.equals(new CharType())) {
            CommandRunner.runCommand(sb, "lw", List.of(shared.wordAcc, String.format("0(%s)", shared.wordAcc)));
            runtime.accType = Coprocessor.WORD;
            runtime.accSyscallType = SyscallType.CHAR;
        }
    }

    private VarType generateForBagCallExtension(ASTNodes.BagCallExtension node, StringBuilder sb, Runtime runtime, VarType calledWithType) throws Exception {
        if (node == null) {
            throw new GeneralDevException("generateForBagCallExtensions called with a null node");
        }
        if (!calledWithType.equals(new BagType("*"))) {
            throw new GeneralDevException("generateForBagCallExtension called on non-bag type " + calledWithType);
        }
        CommandRunner.runCommand(sb, "lw", List.of(shared.wordAcc, String.format("0(%s)", shared.wordAcc)));

        var bag = symbolTable.bagLookup(((BagType) calledWithType).name);
        var field = bag.getField(node.fieldName);
        var fieldIdx = field.first;
        var fieldType = field.second;

        CommandRunner.runCommand(sb, "li", List.of(shared.wordAcc2, fieldIdx.toString()));
        CommandRunner.runCommand(sb, "mul", List.of(shared.wordAcc2, shared.wordAcc2, "4"));
        CommandRunner.runCommand(sb, "mflo", List.of(shared.wordAcc2));

        CommandRunner.runCommand(sb, "add", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));

        if (fieldType.equals(new CharType())) {
            runtime.accType = Coprocessor.WORD;
            runtime.accSyscallType = SyscallType.CHAR;
        } else if (fieldType.equals(new FloatType())) {
            runtime.accType = Coprocessor.FLOAT;
            runtime.accSyscallType = SyscallType.FLOAT;
        } else {
            runtime.accType = Coprocessor.WORD;
            runtime.accSyscallType = SyscallType.INTEGER;
        }

        if (node.callExtension instanceof ASTNodes.ArrayCallExtension) {
            return generateForArrCallExtension((ASTNodes.ArrayCallExtension) node.callExtension, sb, runtime, fieldType);
        } else if (node.callExtension instanceof ASTNodes.BagCallExtension){
            return generateForBagCallExtension((ASTNodes.BagCallExtension) node.callExtension, sb, runtime, fieldType);
        }
        return fieldType;
    }

    private VarType generateForArrCallExtension(ASTNodes.ArrayCallExtension node, StringBuilder sb, Runtime runtime, VarType calledWithType) throws Exception {
        if (node == null) {
            throw new GeneralDevException("generateForArrCallExtension called with a null node");
        }
        if (!calledWithType.hasArrExt) {
            throw new GeneralDevException("generateForArrCallExtension called on a non-array type");
        }
        CommandRunner.runCommand(sb, "lw", List.of(shared.wordAcc, String.format("0(%s)", shared.wordAcc)));

        RuntimeHelper.storeAccToTmp(sb, runtime, shared);
        generateForExpression(node.expression, sb, runtime);
        RuntimeHelper.getFromTmpToAcc2(sb, runtime, shared);
        // calculate the index offset in wordAcc
        CommandRunner.runCommand(sb, "mul", List.of(shared.wordAcc, shared.wordAcc, "4"));
        CommandRunner.runCommand(sb, "mflo", List.of(shared.wordAcc));

        // we have the address in wordAcc2, add the index offset in wordAcc to wordAcc2 to get the final address
        CommandRunner.runCommand(sb, "add", List.of(shared.wordAcc, shared.wordAcc, shared.wordAcc2));

        var drilledType = VarType.arrayDrilled(calledWithType);

        if (drilledType.equals(new CharType())) {
            runtime.accType = Coprocessor.WORD;
            runtime.accSyscallType = SyscallType.CHAR;
        } else if (drilledType.equals(new FloatType())) {
            runtime.accType = Coprocessor.FLOAT;
            runtime.accSyscallType = SyscallType.FLOAT;
        } else {
            runtime.accType = Coprocessor.WORD;
            runtime.accSyscallType = SyscallType.INTEGER;
        }

        if (node.callExtension instanceof ASTNodes.BagCallExtension) {
            return generateForBagCallExtension((ASTNodes.BagCallExtension) node.callExtension, sb, runtime, drilledType);
        }
        return drilledType;
    }

    private VarType generateForVariable(ASTNodes.Variable node, StringBuilder sb, Runtime runtime) throws Exception {
        if (node == null) {
            throw new GeneralDevException("generateForVariable called with a null node");
        }
        Integer varAddress = runtime.getVariable(node.name);
        var varType = symbolTable.varLookup(node.scope, node.name, node.time).type;

        CommandRunner.runCommand(sb, "add", List.of(shared.wordAcc, "$sp", varAddress.toString()));
        runtime.accType = Coprocessor.WORD;
        runtime.accSyscallType = SyscallType.INTEGER;

        if (node.callExtension instanceof ASTNodes.ArrayCallExtension) {
            return generateForArrCallExtension((ASTNodes.ArrayCallExtension) node.callExtension, sb, runtime, varType);
        } else if (node.callExtension instanceof ASTNodes.BagCallExtension){
            return generateForBagCallExtension((ASTNodes.BagCallExtension) node.callExtension, sb, runtime, varType);
        }

        return varType;
    }

//    private void generateForIdentifier(ASTNodes.Identifier node, StringBuilder sb, Runtime runtime) {
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

    private void storeAccToIn(StringBuilder sb, Runtime runtime) {
        if (runtime.accType == Coprocessor.WORD) {
            runtime.inType = Coprocessor.WORD;
            sb.append("move ").append(shared.inWordRegister).append(", ").append(shared.wordAcc).append("\n");
        } else if (runtime.accType == Coprocessor.FLOAT) {
            runtime.inType = Coprocessor.FLOAT;
            sb.append("mov.s ").append(shared.inFloatRegister).append(", ").append(shared.floatAcc).append("\n");
        } else {
            runtime.inType = null;
        }
    }

    private void convertAccumulatorsToSameType(StringBuilder sb, Runtime runtime) {
        if (runtime.accType == Coprocessor.FLOAT || runtime.acc2Type == Coprocessor.FLOAT) {
            if (runtime.accType == Coprocessor.WORD) {
                CommandRunner.runCommand(sb, "mtc1", List.of(shared.wordAcc, shared.floatAcc));
                CommandRunner.runCommand(sb, "cvt.s.w", List.of(shared.floatAcc, shared.floatAcc));
                runtime.accType = Coprocessor.FLOAT;
            } else if (runtime.acc2Type == Coprocessor.WORD) {
                CommandRunner.runCommand(sb, "mtc1", List.of(shared.wordAcc2, shared.floatAcc2));
                CommandRunner.runCommand(sb, "cvt.s.w", List.of(shared.floatAcc2, shared.floatAcc2));
                runtime.acc2Type = Coprocessor.FLOAT;
            }
        }
    }

    private void getLocalVariables(SymbolTable.Table table, List<String> localVariables) {
        if (table == null) {
            return;
        }

        for (SymbolTable.VarInfo varInfo : table.row.values()) {
            if (varInfo.isArgument) {
                continue;
            }

            localVariables.add(varInfo.name);
        }

        SymbolTable.Table child = (SymbolTable.Table) table.firstChild;
        while (child != null) {
            getLocalVariables(child, localVariables);
            child = (SymbolTable.Table) child.neighbor;
        }
    }

    private void getFunctionParameters(List<SymbolTable.FunParam> funParams, List<String> arguments) {
        for (SymbolTable.FunParam param : funParams) {
            arguments.add(param.name);
        }
    }

    private void createActivationRecords() throws Exception {
        for(SymbolTable.FunInfo funInfo : symbolTable.root.funRow.values()) {
            List<String> arguments = new ArrayList<>();
            List<String> localVariables = new ArrayList<>();

            getFunctionParameters(funInfo.params, arguments);

            var table = (SymbolTable.Table) symbolTable.findScope(funInfo.scopeId);
            getLocalVariables(table, localVariables);

            Coprocessor returnType = funInfo.returnType.equals(new FloatType()) ? Coprocessor.FLOAT : Coprocessor.WORD;

            functionActivationRecords.put(funInfo.funName, () -> new Runtime(arguments, localVariables, returnType));
        }
    }

    public String generate(AbstractSyntaxTree tree) throws Exception {
        StringBuilder result = new StringBuilder();
        createActivationRecords();
        generateForProgram((ASTNodes.Program) tree.root, result);
        return result.toString();
    }
}
