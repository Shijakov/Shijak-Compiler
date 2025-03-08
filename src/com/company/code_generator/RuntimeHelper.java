package com.company.code_generator;

import com.company.dev_exceptions.ErrorInGenerationException;

import java.util.List;

public class RuntimeHelper {

    public static void storeAccToTmp(StringBuilder sb, Runtime runtime, SharedRuntime shared) {
        if (runtime.accType == Coprocessor.WORD) {
            String freeRegister = runtime.getNextFreeInt();
            if (freeRegister == null) {
                CommandRunner.runCommand(sb, "addi", List.of("$sp", "$sp", "-4"));
                CommandRunner.runCommand(sb, "sw", List.of(shared.wordAcc, "0($sp)"));
            } else {
                CommandRunner.runCommand(sb, "move", List.of(freeRegister, shared.wordAcc));
            }
        } else if (runtime.accType == Coprocessor.FLOAT){
            String freeRegister = runtime.getNextFreeFloat();
            if (freeRegister == null) {
                CommandRunner.runCommand(sb, "addi", List.of("$sp", "$sp", "-4"));
                CommandRunner.runCommand(sb, "s.s", List.of(shared.floatAcc, "0($sp)"));
            } else {
                CommandRunner.runCommand(sb, "mov.s", List.of(freeRegister, shared.floatAcc));
            }
        }
    }

    public static void getFromTmpToAcc2(StringBuilder sb, Runtime runtime, SharedRuntime shared) throws ErrorInGenerationException {
        String usedTmp = runtime.popUsedRegisters();
        if (usedTmp.equals("SF")) {
            CommandRunner.runCommand(sb, "l.s", List.of(shared.floatAcc2, "0($sp)"));
            CommandRunner.runCommand(sb, "addi", List.of("$sp", "$sp", "4"));
            runtime.acc2Type = Coprocessor.FLOAT;
        } else if (usedTmp.equals("SI")) {
            CommandRunner.runCommand(sb, "lw", List.of(shared.wordAcc2, "0($sp)"));
            CommandRunner.runCommand(sb, "addi", List.of("$sp", "$sp", "4"));
            runtime.acc2Type = Coprocessor.WORD;
        } else if (usedTmp.startsWith("$f")) {
            sb.append("mov.s ").append(shared.floatAcc2).append(", ").append(usedTmp).append("\n");
            runtime.acc2Type = Coprocessor.FLOAT;
        } else if (usedTmp.startsWith("$t")) {
            sb.append("move ").append(shared.wordAcc2).append(", ").append(usedTmp).append("\n");
            runtime.acc2Type = Coprocessor.WORD;
        } else {
            throw new ErrorInGenerationException("getFromTmpToAcc2 received unexpected symbol: " + usedTmp);
        }
    }
}
