package com.company.exceptions;

public class UnknownSymbolException extends LineInfoException {
    public UnknownSymbolException(String symbol, int line) {
        super("Unknown symbol: " + symbol, line);
    }
}
