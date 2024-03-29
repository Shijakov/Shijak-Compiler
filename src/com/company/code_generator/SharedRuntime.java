package com.company.code_generator;

import java.util.*;

public class SharedRuntime {
    final String wordAcc = "$a0";
    final String wordAcc2 = "$a1";
    final String floatAcc = "$f24";
    final String floatAcc2 = "$f22";
    final String inWordRegister = "$s0";
    final String inFloatRegister = "$f20";
    final String syscallRegister = "$v0";
    final String printInteger = "1";
    final String printFloat = "2";
    final String printChar = "11";
    final String inputInteger = "5";
    final String inputFloat = "6";
    final String inputChar = "12";
    final String allocateHeapMemoryCode = "9";
    final String wordInputResult = "$v0";
    final String floatInputResult = "$f0";
    final String integerToPrint = "$a0";
    final String floatToPrint = "$f12";
    final String charToPrint = "$a0";
    final String returnWordRegister = "$v1";
    final String returnFloatRegister = "$f26";
    final String heapMoveRegister = "$s1";
    final String heapStartLabel = "heapStart";
    final String heapEndLabel = "heapEnd";
    final String heapMemReturnRegister = "$s3";
    final String heapStartRegister = "$s2";
    final String heapTmpPointer = "$s4";
    final String heapMemSizeRequestRegister = "$s5";
    final String heapArithmeticTmp = "$t0";
    Map<String, Coprocessor> functionReturnTypes = new HashMap<>();
    int ifCount = 0;
    int whileCount = 0;
    Stack<Integer> whileStack = new Stack<>();
    Set<String> usedFloatConsts = new HashSet<>();

    public void pushToWhileStack() {
        whileStack.push(whileCount);
        whileCount += 1;
    }

    public void popWhile() {
        whileStack.pop();
    }

    public String peekLastWhile() {
        return String.format("$while%d", whileStack.peek());
    }

    public String peekLastEndWhile() {
        return String.format("$endwhile%d", whileStack.peek());
    }
}
