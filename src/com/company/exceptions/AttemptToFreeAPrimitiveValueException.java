package com.company.exceptions;

public class AttemptToFreeAPrimitiveValueException extends Exception{
    public AttemptToFreeAPrimitiveValueException(String identifier) {
        super(String.format("Attempt to free variable %s which is of primitive type", identifier));
    }
}
