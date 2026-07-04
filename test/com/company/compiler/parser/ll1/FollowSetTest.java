package com.company.compiler.parser.ll1;

import com.company.compiler.common.grammar.GrammarBuilder;
import com.company.compiler.common.symbol.EmptySymbol;
import com.company.compiler.common.symbol.StartSymbol;
import com.company.compiler.common.token.TerminalToken;
import com.company.compiler.shijak.ShijakGrammar;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static com.company.compiler.helpers.Helpers.*;
import static com.company.compiler.shijak.ShijakNonTerminals.*;
import static com.company.compiler.shijak.ShijakTokens.*;

public class FollowSetTest {
    @Test
    public void testSingleNonTerminal() {
        var S = nonTerminal("S");
        var A = nonTerminal("A");
        var a = terminal("a");
        var b = terminal("b");
        var c = terminal("c");

        var grammar = GrammarBuilder.grammar(S)
                .withAdded(rule(S, List.of(a, A, b)))
                .withAdded(rule(A, List.of(c)))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(2, followSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(S)
        );

        assertEquals(
                Set.of(b),
                followSet.getFor(A)
        );
    }

    @Test
    public void testEmptyProduction() {
        var S = nonTerminal("S");
        var A = nonTerminal("A");
        var b = terminal("b");

        var grammar = GrammarBuilder.grammar(S)
                .withAdded(rule(S, List.of(A, b)))
                .withAdded(rule(A, List.of(new EmptySymbol())))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(2, followSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(S)
        );

        assertEquals(
                Set.of(b),
                followSet.getFor(A)
        );
    }

    @Test
    public void testNonTerminalAtEnd() {
        var S = nonTerminal("S");
        var A = nonTerminal("A");
        var a = terminal("a");
        var b = terminal("b");

        var grammar = GrammarBuilder.grammar(S)
                .withAdded(rule(S, List.of(a, A)))
                .withAdded(rule(A, List.of(b)))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(2, followSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(S)
        );

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(A)
        );
    }

    @Test
    public void testMultipleOccurrences() {
        var S = nonTerminal("S");
        var A = nonTerminal("A");
        var B = nonTerminal("B");
        var C = nonTerminal("C");
        var a = terminal("a");
        var b = terminal("b");
        var c = terminal("c");

        var grammar = GrammarBuilder.grammar(S)
                .withAdded(rule(S, List.of(A, B, C)))
                .withAdded(rule(A, List.of(a)))
                .withAdded(rule(B, List.of(b)))
                .withAdded(rule(C, List.of(c)))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(4, followSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(S)
        );

        assertEquals(
                Set.of(b),
                followSet.getFor(A)
        );

        assertEquals(
                Set.of(c),
                followSet.getFor(B)
        );

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(C)
        );
    }

    @Test
    public void testEmptyInMiddle() {
        var S = nonTerminal("S");
        var A = nonTerminal("A");
        var B = nonTerminal("B");
        var C = nonTerminal("C");
        var a = terminal("a");
        var c = terminal("c");

        var grammar = GrammarBuilder.grammar(S)
                .withAdded(rule(S, List.of(A, B, C)))
                .withAdded(rule(A, List.of(a)))
                .withAdded(rule(B, List.of(new EmptySymbol())))
                .withAdded(rule(C, List.of(c)))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(4, followSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(S)
        );

        assertEquals(
                Set.of(c),
                followSet.getFor(A)
        );

        assertEquals(
                Set.of(c),
                followSet.getFor(B)
        );

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(C)
        );
    }

    @Test
    public void testRecursiveProduction() {
        var S = nonTerminal("S");
        var A = nonTerminal("A");
        var B = nonTerminal("B");
        var a = terminal("a");

        var grammar = GrammarBuilder.grammar(S)
                .withAdded(rule(S, List.of(A)))
                .withAdded(rule(A, List.of(B)))
                .withAdded(rule(B, List.of(new EmptySymbol())))
                .withAdded(rule(B, List.of(a)))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(3, followSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(S)
        );

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(A)
        );

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(B)
        );
    }

    @Test
    public void testMutualRecursion() {
        var S = nonTerminal("S");
        var A = nonTerminal("A");
        var B = nonTerminal("B");
        var a = terminal("a");
        var b = terminal("b");

        var grammar = GrammarBuilder.grammar(S)
                .withAdded(rule(S, List.of(A, B)))
                .withAdded(rule(A, List.of(B, a)))
                .withAdded(rule(A, List.of(new EmptySymbol())))
                .withAdded(rule(B, List.of(b)))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(3, followSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(S)
        );

        assertEquals(
                Set.of(b),
                followSet.getFor(A)
        );

        assertEquals(
                Set.of(a, new TerminalToken()),
                followSet.getFor(B)
        );
    }

    @Test
    public void testStartSymbolOnly() {
        var A = nonTerminal("A");
        var a = terminal("a");

        var grammar = GrammarBuilder.grammar(A)
                .withAdded(rule(A, List.of(a)))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(1, followSet.size());

        assertEquals(
                Set.of(new TerminalToken()),
                followSet.getFor(A)
        );
    }

    @Test
    public void testClassicExpressionGrammar() {
        var E = nonTerminal("E");
        var Ep = nonTerminal("Ep");
        var T = nonTerminal("T");
        var Tp = nonTerminal("Tp");
        var F = nonTerminal("F");
        var plus = terminal("+");
        var times = terminal("*");
        var open = terminal("(");
        var close = terminal(")");
        var id = terminal("id");

        var grammar = GrammarBuilder.grammar(E)
                .withAdded(rule(E, List.of(T, Ep)))
                .withAdded(rule(Ep, List.of(plus, T, Ep)))
                .withAdded(rule(Ep, List.of(new EmptySymbol())))
                .withAdded(rule(T, List.of(F, Tp)))
                .withAdded(rule(Tp, List.of(times, F, Tp)))
                .withAdded(rule(Tp, List.of(new EmptySymbol())))
                .withAdded(rule(F, List.of(open, E, close)))
                .withAdded(rule(F, List.of(id)))
                .build();

        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));

        assertEquals(5, followSet.size());

        assertEquals(
                Set.of(new TerminalToken(), close),
                followSet.getFor(E)
        );

        assertEquals(
                Set.of(new TerminalToken(), close),
                followSet.getFor(Ep)
        );

        assertEquals(
                Set.of(new TerminalToken(), plus, close),
                followSet.getFor(T)
        );

        assertEquals(
                Set.of(new TerminalToken(), plus, close),
                followSet.getFor(Tp)
        );

        assertEquals(
                Set.of(new TerminalToken(), plus, times, close),
                followSet.getFor(F)
        );
    }
    
    @Test
    public void testShijakGrammar() {
        var terminalToken = new TerminalToken();
        
        var grammar = ShijakGrammar.get();
        var followSet = FollowSet.from(grammar, FirstSet.from(grammar));
        
        assertEquals(50, followSet.size());

        assertEquals(
                Set.of(terminalToken),
                followSet.getFor(PROGRAM)
        );

        assertEquals(
                Set.of(terminalToken, functionToken, bagToken),
                followSet.getFor(DEFINITION)
        );

        assertEquals(
                Set.of(closingCurlyBracketToken),
                followSet.getFor(DEFINITION_ASSIGNMENT)
        );

        assertEquals(
                Set.of(terminalToken),
                followSet.getFor(FUNCTION_OR_BAG)
        );

        assertEquals(
                Set.of(terminalToken, functionToken, bagToken),
                followSet.getFor(BAG_DEFINITION)
        );

        assertEquals(
                Set.of(closingCurlyBracketToken),
                followSet.getFor(BAG_DEFINITION_PARAMETER_LIST)
        );

        assertEquals(
                Set.of(closingCurlyBracketToken),
                followSet.getFor(BAG_DEFINITION_PARAMETER_LIST_TAIL)
        );

        assertEquals(
                Set.of(terminalToken, functionToken, bagToken),
                followSet.getFor(FUNCTION)
        );

        assertEquals(
                Set.of(closingBracketToken),
                followSet.getFor(PARAM_LIST)
        );

        assertEquals(
                Set.of(closingBracketToken),
                followSet.getFor(PARAM_LIST_TAIL)
        );

        //'',while,if,break,continue,let,input,output,alloc,free,return,fill,!,(,in,identifier,char_constant,bool_constant,int_constant,float_constant
        //'',while,if,break,continue,let,input,output,alloc,free,return,fill,!,(,in,identifier,char_constant,bool_constant,int_constant,float_constant
        assertEquals(
                Set.of(closingCurlyBracketToken),
                followSet.getFor(STATEMENT_LIST)
        );

        // },while,if,break,continue,let,input,output,alloc,free,return,fill,!,(,in,identifier,char_constant,bool_constant,int_constant,float_constant
        assertEquals(
                Set.of(closingCurlyBracketToken, whileToken, ifToken, breakToken, continueToken, letToken, inputToken, outputToken, allocToken, freeToken, returnToken, fillToken, notToken, openingBracketToken, inToken, nameToken, charConstToken, intConstToken, floatConstToken, falseConstToken, trueConstToken),
                followSet.getFor(STATEMENT)
        );

        assertEquals(
                Set.of(closingCurlyBracketToken, whileToken, ifToken, breakToken, continueToken, letToken, inputToken, outputToken, allocToken, freeToken, returnToken, fillToken, notToken, openingBracketToken, inToken, nameToken, charConstToken, intConstToken, floatConstToken, falseConstToken, trueConstToken),
                followSet.getFor(FILL_STATEMENT)
        );

        assertEquals(
                Set.of(closingCurlyBracketToken, whileToken, ifToken, breakToken, continueToken, letToken, inputToken, outputToken, allocToken, freeToken, returnToken, fillToken, notToken, openingBracketToken, inToken, nameToken, charConstToken, intConstToken, floatConstToken, falseConstToken, trueConstToken),
                followSet.getFor(DEFINE_VAR)
        );

        assertEquals(
                Set.of(closingCurlyBracketToken, whileToken, ifToken, breakToken, continueToken, letToken, inputToken, outputToken, allocToken, freeToken, returnToken, fillToken, notToken, openingBracketToken, inToken, nameToken, charConstToken, intConstToken, floatConstToken, falseConstToken, trueConstToken),
                followSet.getFor(ALLOC_ARR)
        );

        assertEquals(
                Set.of(closingCurlyBracketToken, whileToken, ifToken, breakToken, continueToken, letToken, inputToken, outputToken, allocToken, freeToken, returnToken, fillToken, notToken, openingBracketToken, inToken, nameToken, charConstToken, intConstToken, floatConstToken, falseConstToken, trueConstToken),
                followSet.getFor(FREE_ARR)
        );

        assertEquals(
                Set.of(closingCurlyBracketToken, whileToken, ifToken, breakToken, continueToken, letToken, inputToken, outputToken, allocToken, freeToken, returnToken, fillToken, notToken, openingBracketToken, inToken, nameToken, charConstToken, intConstToken, floatConstToken, falseConstToken, trueConstToken),
                followSet.getFor(RETURN_STATEMENT)
        );

        assertEquals(
                Set.of(closingCurlyBracketToken, whileToken, ifToken, breakToken, continueToken, letToken, inputToken, outputToken, allocToken, freeToken, returnToken, fillToken, notToken, openingBracketToken, inToken, nameToken, charConstToken, intConstToken, floatConstToken, falseConstToken, trueConstToken),
                followSet.getFor(BREAK_STATEMENT)
        );

        assertEquals(
                Set.of(closingCurlyBracketToken, whileToken, ifToken, breakToken, continueToken, letToken, inputToken, outputToken, allocToken, freeToken, returnToken, fillToken, notToken, openingBracketToken, inToken, nameToken, charConstToken, intConstToken, floatConstToken, falseConstToken, trueConstToken),
                followSet.getFor(CONTINUE_STATEMENT)
        );

        assertEquals(
                Set.of(closingCurlyBracketToken, whileToken, ifToken, breakToken, continueToken, letToken, inputToken, outputToken, allocToken, freeToken, returnToken, fillToken, notToken, openingBracketToken, inToken, nameToken, charConstToken, intConstToken, floatConstToken, falseConstToken, trueConstToken),
                followSet.getFor(INPUT_STATEMENT)
        );

        assertEquals(
                Set.of(closingCurlyBracketToken, whileToken, ifToken, breakToken, continueToken, letToken, inputToken, outputToken, allocToken, freeToken, returnToken, fillToken, notToken, openingBracketToken, inToken, nameToken, charConstToken, intConstToken, floatConstToken, falseConstToken, trueConstToken),
                followSet.getFor(OUTPUT_STATEMENT)
        );

        assertEquals(
                Set.of(closingCurlyBracketToken, whileToken, ifToken, breakToken, continueToken, letToken, inputToken, outputToken, allocToken, freeToken, returnToken, fillToken, notToken, openingBracketToken, inToken, nameToken, charConstToken, intConstToken, floatConstToken, falseConstToken, trueConstToken),
                followSet.getFor(WHILE_STATEMENT)
        );

        assertEquals(
                Set.of(closingCurlyBracketToken, whileToken, ifToken, breakToken, continueToken, letToken, inputToken, outputToken, allocToken, freeToken, returnToken, fillToken, notToken, openingBracketToken, inToken, nameToken, charConstToken, intConstToken, floatConstToken, falseConstToken, trueConstToken),
                followSet.getFor(IF_STATEMENT)
        );

        assertEquals(
                Set.of(elseToken, closingCurlyBracketToken, whileToken, ifToken, breakToken, continueToken, letToken, inputToken, outputToken, allocToken, freeToken, returnToken, fillToken, notToken, openingBracketToken, inToken, nameToken, charConstToken, intConstToken, floatConstToken, falseConstToken, trueConstToken),
                followSet.getFor(ELIF_STATEMENT)
        );

        assertEquals(
                Set.of(closingCurlyBracketToken, whileToken, ifToken, breakToken, continueToken, letToken, inputToken, outputToken, allocToken, freeToken, returnToken, fillToken, notToken, openingBracketToken, inToken, nameToken, charConstToken, intConstToken, floatConstToken, falseConstToken, trueConstToken),
                followSet.getFor(ELSE_STATEMENT)
        );

        assertEquals(
                Set.of(closingCurlyBracketToken, whileToken, ifToken, breakToken, continueToken, letToken, inputToken, outputToken, allocToken, freeToken, returnToken, fillToken, notToken, openingBracketToken, inToken, nameToken, charConstToken, intConstToken, floatConstToken, falseConstToken, trueConstToken),
                followSet.getFor(EXPRESSION_LIST)
        );

        assertEquals(
                Set.of(semiColonToken),
                followSet.getFor(EXPRESSION_LIST_TAIL)
        );

        // {return,eq,!,(,in,identifier,char_constant,bool_constant,int_constant,float_constant}
        assertEquals(
                Set.of(semiColonToken),
                followSet.getFor(EXPRESSION_OR_CLOSER)
        );

        assertEquals(
                Set.of(semiColonToken),
                followSet.getFor(CLOSER)
        );

        // {;,),>>,},while,if,break,continue,let,input,output,alloc,free,return,fill,!,(,in,identifier,char_constant,bool_constant,int_constant,float_constant,,}
        assertEquals(
                Set.of(semiColonToken, closingBracketToken, expressionCombinerToken, commaToken),
                followSet.getFor(EXPRESSION)
        );

        // {>=,<=,<,>,==,!=,''}
        assertEquals(
                Set.of(semiColonToken, closingBracketToken, expressionCombinerToken, commaToken),
                followSet.getFor(EXPRESSION_TAIL)
        );

        // {!,(,in,identifier,char_constant,bool_constant,int_constant,float_constant}
        assertEquals(
                Set.of(semiColonToken, closingBracketToken, expressionCombinerToken, commaToken, closingSquareBracketToken, greaterThanOrEqualToken, greaterThanToken, lessThanOrEqualToken, lessThanToken, equalToken, notEqualToken),
                followSet.getFor(MOD_EXPR)
        );

        // {%,''}
        assertEquals(
                Set.of(semiColonToken, closingBracketToken, expressionCombinerToken, commaToken, closingSquareBracketToken, greaterThanOrEqualToken, greaterThanToken, lessThanOrEqualToken, lessThanToken, equalToken, notEqualToken),
                followSet.getFor(MOD_EXPR_TAIL)
        );

        // {!,(,in,identifier,char_constant,bool_constant,int_constant,float_constant}
        assertEquals(
                Set.of(moduloToken, semiColonToken, closingBracketToken, expressionCombinerToken, commaToken, closingSquareBracketToken, greaterThanOrEqualToken, greaterThanToken, lessThanOrEqualToken, lessThanToken, equalToken, notEqualToken),
                followSet.getFor(ADD_EXPR)
        );
        // {+,-,||,''}
        assertEquals(
                Set.of(moduloToken, semiColonToken, closingBracketToken, expressionCombinerToken, commaToken, closingSquareBracketToken, greaterThanOrEqualToken, greaterThanToken, lessThanOrEqualToken, lessThanToken, equalToken, notEqualToken),
                followSet.getFor(ADD_EXPR_TAIL)
        );

        assertEquals(
                Set.of(plusToken, minusToken, orToken, moduloToken, semiColonToken, closingBracketToken, expressionCombinerToken, commaToken, closingSquareBracketToken, greaterThanOrEqualToken, greaterThanToken, lessThanOrEqualToken, lessThanToken, equalToken, notEqualToken),
                followSet.getFor(MUL_EXPR)
        );

        // {*,/,&&,''}
        assertEquals(
                Set.of(plusToken, minusToken, orToken, moduloToken, semiColonToken, closingBracketToken, expressionCombinerToken, commaToken, closingSquareBracketToken, greaterThanOrEqualToken, greaterThanToken, lessThanOrEqualToken, lessThanToken, equalToken, notEqualToken),
                followSet.getFor(MUL_EXPR_TAIL)
        );

        assertEquals(
                Set.of(multiplicationToken, divisionToken, andToken, plusToken, minusToken, orToken, moduloToken, semiColonToken, closingBracketToken, expressionCombinerToken, commaToken, closingSquareBracketToken, greaterThanOrEqualToken, greaterThanToken, lessThanOrEqualToken, lessThanToken, equalToken, notEqualToken),
                followSet.getFor(PRIMARY)
        );

        assertEquals(
                Set.of(multiplicationToken, divisionToken, andToken, plusToken, minusToken, orToken, moduloToken, semiColonToken, closingBracketToken, expressionCombinerToken, commaToken, closingSquareBracketToken, greaterThanOrEqualToken, greaterThanToken, lessThanOrEqualToken, lessThanToken, equalToken, notEqualToken),
                followSet.getFor(INSTANCE)
        );

        // {(,[,.,''}
        assertEquals(
                Set.of(multiplicationToken, divisionToken, andToken, plusToken, minusToken, orToken, moduloToken, semiColonToken, closingBracketToken, expressionCombinerToken, commaToken, closingSquareBracketToken, greaterThanOrEqualToken, greaterThanToken, lessThanOrEqualToken, lessThanToken, equalToken, notEqualToken),
                followSet.getFor(INSTANCE_TAIL)
        );

        assertEquals(
                Set.of(semiColonToken),
                followSet.getFor(ASSIGNABLE_INSTANCE)
        );

        // {[,.,''}
        assertEquals(
                Set.of(multiplicationToken, divisionToken, andToken, plusToken, minusToken, orToken, moduloToken, semiColonToken, closingBracketToken, expressionCombinerToken, commaToken, closingSquareBracketToken, greaterThanOrEqualToken, greaterThanToken, lessThanOrEqualToken, lessThanToken, equalToken, notEqualToken),
                followSet.getFor(ASSIGNABLE_INSTANCE_TAIL)
        );

        // {.,''}
        assertEquals(
                Set.of(multiplicationToken, divisionToken, andToken, plusToken, minusToken, orToken, moduloToken, semiColonToken, closingBracketToken, expressionCombinerToken, commaToken, closingSquareBracketToken, greaterThanOrEqualToken, greaterThanToken, lessThanOrEqualToken, lessThanToken, equalToken, notEqualToken),
                followSet.getFor(DOT_TAIL)
        );

        // {'',!,(,in,identifier,char_constant,bool_constant,int_constant,float_constant}
        assertEquals(
                Set.of(closingBracketToken),
                followSet.getFor(EXPR_LIST)
        );

        // {,,''}
        assertEquals(
                Set.of(closingBracketToken),
                followSet.getFor(EXPR_LIST_TAIL)
        );

        // {void,int,float,char,bool,bag}
        assertEquals(
                Set.of(openingCurlyBracketToken),
                followSet.getFor(RETURN_TYPE)
        );

        // {,,},),;,{}
        assertEquals(
                Set.of(commaToken, closingCurlyBracketToken, closingBracketToken, semiColonToken, openingCurlyBracketToken),
                followSet.getFor(TYPE)
        );

        // {[,''}
        assertEquals(
                Set.of(commaToken, closingCurlyBracketToken, closingBracketToken, semiColonToken, openingCurlyBracketToken),
                followSet.getFor(ARRAY_EMPTY_EXTENSION)
        );

        // 	{[,,,},),;,{}
        assertEquals(
                Set.of(openingSquareBracketToken, commaToken, closingCurlyBracketToken, closingBracketToken, semiColonToken, openingCurlyBracketToken),
                followSet.getFor(PRIMITIVE_TYPE)
        );

        // >>, ], ;, ), >=, <=, <, >, ==, !=, %, +, -, ||, *, /, &&, ,
        assertEquals(
                Set.of(expressionCombinerToken, closingSquareBracketToken, semiColonToken, closingBracketToken, greaterThanOrEqualToken, lessThanOrEqualToken, greaterThanToken, lessThanToken,
                        equalToken, notEqualToken, moduloToken, plusToken, minusToken, orToken, multiplicationToken, divisionToken, andToken, commaToken),
                followSet.getFor(PRIMITIVE_CONSTANT)
        );
    }
}
