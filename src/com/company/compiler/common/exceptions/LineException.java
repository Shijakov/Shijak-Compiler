package com.company.compiler.common.exceptions;

public class LineException extends ProgramException {
    public LineException(String message, Integer line) {
        super(message + " at line " + line);
    }
}
