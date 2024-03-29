package com.company.exceptions;

public class UnknownSymbolException extends Exception{
    public UnknownSymbolException(String symbol) {
        super(String.format("Unknown symbol: %s", symbol));
    }
}
