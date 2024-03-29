package com.company.semantic_analyzer;

public class FunctionNode {
    public FunctionNode parent;
    public FunctionNode firstChild;
    public FunctionNode neighbor;
    public boolean returns;
    public boolean isElse;

    public void print(int n) {
        System.out.print("-".repeat(n) + " N:");
        if (returns) {
            System.out.print("R");
        }
        if (isElse) {
            System.out.print("E");
        }
        System.out.println();
        FunctionNode next = firstChild;
        while (next != null) {
            next.print(n + 1);
            next = next.neighbor;
        }
    }
}
