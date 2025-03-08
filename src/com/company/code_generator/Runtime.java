package com.company.code_generator;

import com.company.model.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Runtime {
    Stack<String> freeFloatRegisters;
    Stack<String> freeWordRegisters;
    Stack<String> usedRegisters;
    Coprocessor inType;
    Coprocessor accType;
    Coprocessor acc2Type;
    SyscallType accSyscallType;
    final Coprocessor returnType;

    public Map<String, Integer> storedRegisters;

    public Map<String, Integer> arguments;
    public Map<String, Integer> localVariables;
    int stackPointerOffset;

    public Runtime(List<String> arguments, List<String> localVariables, Coprocessor returnType) {
        freeFloatRegisters = new Stack<>();
        freeFloatRegisters.push("$f18");
        freeFloatRegisters.push("$f16");
        freeFloatRegisters.push("$f10");
        freeFloatRegisters.push("$f8");
        freeFloatRegisters.push("$f6");
        freeFloatRegisters.push("$f4");
        freeFloatRegisters.push("$f2");

        freeWordRegisters = new Stack<>();
        freeWordRegisters.push("$t9");
        freeWordRegisters.push("$t8");
        freeWordRegisters.push("$t7");
        freeWordRegisters.push("$t6");
        freeWordRegisters.push("$t5");
        freeWordRegisters.push("$t4");
        freeWordRegisters.push("$t3");
        freeWordRegisters.push("$t2");
        freeWordRegisters.push("$t1");
        freeWordRegisters.push("$t0");

        this.storedRegisters = new HashMap<>();
        this.arguments = new HashMap<>();
        this.localVariables = new HashMap<>();
        stackPointerOffset = 0;

        for (String argument : arguments) {
            stackPointerOffset += 4;
            this.arguments.put(argument, stackPointerOffset);
        }

        for (String localVariable : localVariables) {
            stackPointerOffset += 4;
            this.localVariables.put(localVariable, stackPointerOffset);
        }

        stackPointerOffset += 4;
        this.storedRegisters.put("$ra", stackPointerOffset);

        for (String wordRegister : freeWordRegisters) {
            stackPointerOffset += 4;
            this.storedRegisters.put(wordRegister, stackPointerOffset);
        }

        for (String floatRegister : freeFloatRegisters) {
            stackPointerOffset += 4;
            this.storedRegisters.put(floatRegister, stackPointerOffset);
        }

        usedRegisters = new Stack<>();

        inType = null;
        accType = null;
        acc2Type = null;
        accSyscallType = null;
        this.returnType = returnType;
    }

    public void bootstrap(StringBuilder sb) {
        int tmp = (localVariables.size() + storedRegisters.size()) * 4;
        CommandRunner.runCommand(sb, "addi", List.of("$sp", "$sp", String.valueOf(tmp * -1)));
    }

    public void saveRegisters(StringBuilder sb) {
        for (String register : usedRegisters) {
            if (register.startsWith("S")) {
                continue;
            }
            int stackOffset = storedRegisters.get(register);
            if (register.startsWith("$f")) {
                CommandRunner.runCommand(sb, "s.s", List.of(register, String.format("%d($sp)", stackPointerOffset - stackOffset)));
            } else {
                CommandRunner.runCommand(sb, "sw", List.of(register, String.format("%d($sp)", stackPointerOffset - stackOffset)));
            }
        }
        int stackOffset = storedRegisters.get("$ra");
        CommandRunner.runCommand(sb, "sw", List.of("$ra", String.format("%d($sp)", stackPointerOffset - stackOffset)));
    }

    public void restoreRegisters(StringBuilder sb) {
        for (String register : usedRegisters) {
            if (register.startsWith("S")) {
                continue;
            }
            int stackOffset = storedRegisters.get(register);
            if (register.startsWith("$f")) {
                CommandRunner.runCommand(sb, "l.s", List.of(register, String.format("%d($sp)", stackPointerOffset - stackOffset)));
            } else {
                CommandRunner.runCommand(sb, "lw", List.of(register, String.format("%d($sp)", stackPointerOffset - stackOffset)));
            }
        }
        int stackOffset = storedRegisters.get("$ra");
        CommandRunner.runCommand(sb, "lw", List.of("$ra", String.format("%d($sp)", stackPointerOffset - stackOffset)));
    }

    public void eraseUsedSpace(StringBuilder sb) {
        int usedSpace = (localVariables.size() + arguments.size() + storedRegisters.size()) * 4;
        CommandRunner.runCommand(sb, "addi", List.of("$sp", "$sp", String.valueOf(usedSpace)));
    }

    public Integer getVariable(String name) {
        Integer var = localVariables.get(name);
        if (var == null) {
            var = arguments.get(name);
        }
        return stackPointerOffset - var;
    }

    public String getNextFreeInt() {
        if (freeWordRegisters.empty()) {
            stackPointerOffset += 4;
            usedRegisters.push("SI");
            return null;
        } else {
            usedRegisters.push(freeWordRegisters.peek());
            return freeWordRegisters.pop();
        }
    }

    public String getNextFreeFloat() {
        if (freeFloatRegisters.empty()) {
            stackPointerOffset += 4;
            usedRegisters.push("SF");
            return null;
        } else {
            usedRegisters.push(freeFloatRegisters.peek());
            return freeFloatRegisters.pop();
        }
    }

    public String popUsedRegisters() {
        String register = usedRegisters.pop();
        if (register.startsWith("$f")) {
            freeFloatRegisters.push(register);
        } else if (register.startsWith("$t")) {
            freeWordRegisters.push(register);
        } else {
            stackPointerOffset -= 4;
        }
        return register;
    }

    public void printLocalVariables() {
        for(String variable: localVariables.keySet()) {
            System.out.printf("%s - %d\n", variable, localVariables.get(variable));
        }
    }
}
