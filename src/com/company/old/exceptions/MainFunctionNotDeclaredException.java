package com.company.old.exceptions;

public class MainFunctionNotDeclaredException extends Exception {
    public MainFunctionNotDeclaredException() {
        super("main() function not declared");
    }
}
