package com.company.old.exceptions;

public class UnknownSymbolException extends LineInfoException {
    public UnknownSymbolException(String symbol, int line) {
        super("Unknown symbol: " + symbol, line);
    }
}
