package com.company.exceptions;

public class NotAllPathsHaveAReturnStatementException extends LineInfoException {
    public NotAllPathsHaveAReturnStatementException(String funName, int line) {
        super(String.format("%s - Not all paths have a return statement", funName), line);
    }
}
