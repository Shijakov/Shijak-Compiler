package com.company.dev_exceptions;

public class ScopeNotFoundException extends Exception{
    public ScopeNotFoundException(Integer id) {
        super(String.format("Scope with id %d not found", id));
    }
}
