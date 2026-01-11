package com.company.compiler.common.token;

public class RecognisedToken extends Token {
    protected final String value;
    protected final int line;

    protected RecognisedToken(String regex, String value, int line) {
        super(regex);
        this.value = value;
        this.line = line;
    }

    public static RecognisedToken match(Token token, String value, int line) {
        return new RecognisedToken(token.getRegex(), value, line);
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }
}
