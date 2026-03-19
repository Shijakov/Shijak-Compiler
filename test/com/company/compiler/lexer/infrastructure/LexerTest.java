package com.company.compiler.lexer.infrastructure;

import com.company.compiler.common.token.TerminalToken;
import com.company.compiler.lexer.exceptions.TokenNotRecognizedException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static com.company.compiler.helpers.Helpers.*;

class LexerTest {

    private Lexer getLexer() {
        return new LongestTokenLexer();
    }

    @Test
    void testNormalSentence() {
        var tokens = Arrays.asList(
                token("meet"),
                token("meeting"),
                token("you"),
                token("was"),
                token("fun"),
                ignoredToken(" ")
        );
        var program = "meeting you was fun";
        var lexer = getLexer();

        assertEquals(Arrays.asList(
                token("meeting"),
                token("you"),
                token("was"),
                token("fun"),
                new TerminalToken()
        ), lexer.analyze(program, tokens));
    }

    @Test
    void testUndefinedTokenThrowsSyntaxError() {
        var tokens = Arrays.asList(
                token("hi"),
                token("bye"),
                token("nice"),
                token("to"),
                token("meet"),
                token("you"),
                ignoredToken(" ")
        );
        var program = "hi tom nice to meet you";
        var lexer = getLexer();

        assertThrows(TokenNotRecognizedException.class, () -> lexer.analyze(program, tokens));
    }

    @Test
    void testLongestTokenWins() {
        var tokens = Arrays.asList(
                token("meet"),
                token("ing"),
                token("meeting"),
                token("you"),
                token("was"),
                token("fun"),
                ignoredToken(" ")
        );
        var program = "meeting you was fun";
        var lexer = getLexer();

        assertEquals(Arrays.asList(
                token("meeting"),
                token("you"),
                token("was"),
                token("fun"),
                new TerminalToken()
        ), lexer.analyze(program, tokens));
    }

    @Test
    void testPriorityWorks() {
        var tokens = Arrays.asList(
                token("function"),
                identifierToken("[a-zA-Z_]+[a-zA-Z_0-9]*"),
                token("\\("),
                token("\\)"),
                token(","),
                token("int"),
                token("bool"),
                token(":"),
                ignoredToken(" ")
        );
        var program = "function isGreaterThan(a: int, b: int): bool";
        var lexer = getLexer();

        assertEquals(Arrays.asList(
                token("function"),
                token("[a-zA-Z_]+[a-zA-Z_0-9]*"),
                token("\\("),
                token("[a-zA-Z_]+[a-zA-Z_0-9]*"),
                token(":"),
                token("int"),
                token(","),
                token("[a-zA-Z_]+[a-zA-Z_0-9]*"),
                token(":"),
                token("int"),
                token("\\)"),
                token(":"),
                token("bool"),
                new TerminalToken()
        ), lexer.analyze(program, tokens));
    }
}