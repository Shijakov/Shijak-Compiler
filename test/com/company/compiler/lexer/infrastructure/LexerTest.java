package com.company.compiler.lexer.infrastructure;

import com.company.compiler.common.token.TerminalToken;
import com.company.compiler.lexer.exceptions.SyntaxError;
import com.company.compiler.lexer.model.LexerToken;
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
                LexerToken.from(token("meet")),
                LexerToken.from(token("meeting")),
                LexerToken.from(token("you")),
                LexerToken.from(token("was")),
                LexerToken.from(token("fun")),
                LexerToken.ignored(token(" "))
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
                LexerToken.from(token("hi")),
                LexerToken.from(token("bye")),
                LexerToken.from(token("nice")),
                LexerToken.from(token("to")),
                LexerToken.from(token("meet")),
                LexerToken.from(token("you")),
                LexerToken.ignored(token(" "))
        );
        var program = "hi tom nice to meet you";
        var lexer = getLexer();

        assertThrows(SyntaxError.class, () -> lexer.analyze(program, tokens));
    }

    @Test
    void testLongestTokenWins() {
        var tokens = Arrays.asList(
                LexerToken.from(token("meet")),
                LexerToken.from(token("ing")),
                LexerToken.from(token("meeting")),
                LexerToken.from(token("you")),
                LexerToken.from(token("was")),
                LexerToken.from(token("fun")),
                LexerToken.ignored(token(" "))
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
                LexerToken.from(token("function")),
                LexerToken.lowPriority(token("[a-zA-Z_]+[a-zA-Z_0-9]*")),
                LexerToken.from(token("\\(")),
                LexerToken.from(token("\\)")),
                LexerToken.from(token(",")),
                LexerToken.from(token("int")),
                LexerToken.from(token("bool")),
                LexerToken.from(token(":")),
                LexerToken.ignored(token(" "))
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