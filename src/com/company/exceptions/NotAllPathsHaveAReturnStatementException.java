package com.company.exceptions;

public class NotAllPathsHaveAReturnStatementException extends Exception {
    public NotAllPathsHaveAReturnStatementException(String funName) {
        super(String.format("%s - Not all paths have a return statement", funName));
    }
}
