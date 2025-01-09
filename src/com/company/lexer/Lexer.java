package com.company.lexer;
import com.company.exceptions.UnknownSymbolException;
import com.company.model.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    public MyMatcher nameMatcher = new MyMatcher("[a-zA-Z_]+[a-zA-Z_0-9]*");
    public MyMatcher intConstMatcher = new MyMatcher("-?[0-9]+");
    public MyMatcher floatConstMatcher = new MyMatcher("-?[0-9]+\\.[0-9]+");
    public MyMatcher charConstMatcher = new MyMatcher("'.'");
    public MyMatcher trueConstMatcher = new MyMatcher("true");
    public MyMatcher falseConstMatcher = new MyMatcher("false");
    public MyMatcher openingBracketMatcher = new MyMatcher("\\(");
    public MyMatcher closingBracketMatcher = new MyMatcher("\\)");
    public MyMatcher openingCurlyBracketMatcher = new MyMatcher("\\{");
    public MyMatcher closingCurlyBracketMatcher = new MyMatcher("}");
    public MyMatcher openingSquareBracketMatcher = new MyMatcher("\\[");
    public MyMatcher closingSquareBracketMatcher = new MyMatcher("]");
    public MyMatcher expressionCombinerMatcher = new MyMatcher(">>");
    public MyMatcher functionMatcher = new MyMatcher("fun");
    public MyMatcher bagMatcher = new MyMatcher("bag");
    public MyMatcher defineMatcher = new MyMatcher("define");
    public MyMatcher ifMatcher = new MyMatcher("if");
    public MyMatcher elifMatcher = new MyMatcher("elif");
    public MyMatcher elseMatcher = new MyMatcher("else");
    public MyMatcher whileMatcher = new MyMatcher("while");
    public MyMatcher breakMatcher = new MyMatcher("break");
    public MyMatcher continueMatcher = new MyMatcher("continue");
    public MyMatcher outputMatcher = new MyMatcher("output");
    public MyMatcher inputMatcher = new MyMatcher("input");
    public MyMatcher letMatcher = new MyMatcher("let");
    public MyMatcher allocMatcher = new MyMatcher("alloc");
    public MyMatcher fillMatcher = new MyMatcher("fill");
    public MyMatcher freeMatcher = new MyMatcher("free");
    public MyMatcher eqMatcher = new MyMatcher("eq");
    public MyMatcher inMatcher = new MyMatcher("in");
    public MyMatcher semiColonMatcher = new MyMatcher(";");
    public MyMatcher commaMatcher = new MyMatcher(",");
    public MyMatcher dotMatcher = new MyMatcher("\\.");
    public MyMatcher colonMatcher = new MyMatcher(":");
    public MyMatcher plusMatcher = new MyMatcher("\\+");
    public MyMatcher minusMatcher = new MyMatcher("-");
    public MyMatcher equalMatcher = new MyMatcher("==");
    public MyMatcher notEqualMatcher = new MyMatcher("!=");
    public MyMatcher lessThanMatcher = new MyMatcher("<");
    public MyMatcher greaterThanMatcher = new MyMatcher(">");
    public MyMatcher lessThanOrEqualMatcher = new MyMatcher("<=");
    public MyMatcher greaterThanOrEqualMatcher = new MyMatcher(">=");
    public MyMatcher multiplicationMatcher = new MyMatcher("\\*");
    public MyMatcher divisionMatcher = new MyMatcher("/");
    public MyMatcher moduloMatcher = new MyMatcher("%");
    public MyMatcher notMatcher = new MyMatcher("!");
    public MyMatcher andMatcher = new MyMatcher("&&");
    public MyMatcher orMatcher = new MyMatcher("\\|\\|");
    public MyMatcher intMatcher = new MyMatcher("int");
    public MyMatcher floatMatcher = new MyMatcher("float");
    public MyMatcher boolMatcher = new MyMatcher("bool");
    public MyMatcher charMatcher = new MyMatcher("char");
    public MyMatcher voidMatcher = new MyMatcher("void");
    public MyMatcher returnMatcher = new MyMatcher("return");
    public MyMatcher blankMatcher = new MyMatcher("[ \n\t]*");
    public MyMatcher commentMatcher = new MyMatcher("//.*\n");
    public MyMatcher multiLineCommentMatcher = new MyMatcher("/\\*[^(\\*/)]*\\*/");

    List<MyMatcher> matchers;
    Map<MyMatcher, Token.TokenType> matcherTokenMap;

    public Lexer() {
        matchers = new ArrayList<>();
        matchers.add(multiLineCommentMatcher);
        matchers.add(commentMatcher);
        matchers.add(blankMatcher);
        matchers.add(returnMatcher);
        matchers.add(voidMatcher);
        matchers.add(boolMatcher);
        matchers.add(floatMatcher);
        matchers.add(intMatcher);
        matchers.add(charMatcher);
        matchers.add(functionMatcher);
        matchers.add(defineMatcher);
        matchers.add(ifMatcher);
        matchers.add(elifMatcher);
        matchers.add(elseMatcher);
        matchers.add(whileMatcher);
        matchers.add(breakMatcher);
        matchers.add(continueMatcher);
        matchers.add(outputMatcher);
        matchers.add(inputMatcher);
        matchers.add(letMatcher);
        matchers.add(allocMatcher);
        matchers.add(freeMatcher);
        matchers.add(eqMatcher);
        matchers.add(inMatcher);
        matchers.add(multiplicationMatcher);
        matchers.add(greaterThanOrEqualMatcher);
        matchers.add(lessThanOrEqualMatcher);
        matchers.add(greaterThanMatcher);
        matchers.add(lessThanMatcher);
        matchers.add(notEqualMatcher);
        matchers.add(equalMatcher);
        matchers.add(minusMatcher);
        matchers.add(plusMatcher);
        matchers.add(colonMatcher);
        matchers.add(commaMatcher);
        matchers.add(semiColonMatcher);
        matchers.add(orMatcher);
        matchers.add(andMatcher);
        matchers.add(notMatcher);
        matchers.add(moduloMatcher);
        matchers.add(divisionMatcher);
        matchers.add(expressionCombinerMatcher);
        matchers.add(closingSquareBracketMatcher);
        matchers.add(openingSquareBracketMatcher);
        matchers.add(closingCurlyBracketMatcher);
        matchers.add(openingCurlyBracketMatcher);
        matchers.add(closingBracketMatcher);
        matchers.add(openingBracketMatcher);
        matchers.add(trueConstMatcher);
        matchers.add(falseConstMatcher);
        matchers.add(floatConstMatcher);
        matchers.add(intConstMatcher);
        matchers.add(charConstMatcher);
        matchers.add(nameMatcher);
        matchers.add(bagMatcher);
        matchers.add(fillMatcher);
        matchers.add(dotMatcher);

        matcherTokenMap = new HashMap<>();
        matcherTokenMap.put(returnMatcher, Token.TokenType.RETURN_KEYWORD);
        matcherTokenMap.put(voidMatcher, Token.TokenType.VOID_KEYWORD);
        matcherTokenMap.put(boolMatcher, Token.TokenType.BOOL_KEYWORD);
        matcherTokenMap.put(floatMatcher, Token.TokenType.FLOAT_KEYWORD);
        matcherTokenMap.put(intMatcher, Token.TokenType.INT_KEYWORD);
        matcherTokenMap.put(functionMatcher, Token.TokenType.FUNCTION_KEYWORD);
        matcherTokenMap.put(ifMatcher, Token.TokenType.IF_KEYWORD);
        matcherTokenMap.put(elifMatcher, Token.TokenType.ELIF_KEYWORD);
        matcherTokenMap.put(elseMatcher, Token.TokenType.ELSE_KEYWORD);
        matcherTokenMap.put(whileMatcher, Token.TokenType.WHILE_KEYWORD);
        matcherTokenMap.put(breakMatcher, Token.TokenType.BREAK_KEYWORD);
        matcherTokenMap.put(continueMatcher, Token.TokenType.CONTINUE_KEYWORD);
        matcherTokenMap.put(outputMatcher, Token.TokenType.OUTPUT_KEYWORD);
        matcherTokenMap.put(inputMatcher, Token.TokenType.INPUT_KEYWORD);
        matcherTokenMap.put(letMatcher, Token.TokenType.LET_KEYWORD);
        matcherTokenMap.put(allocMatcher, Token.TokenType.ALLOC_KEYWORD);
        matcherTokenMap.put(freeMatcher, Token.TokenType.FREE_KEYWORD);
        matcherTokenMap.put(eqMatcher, Token.TokenType.EQ_KEYWORD);
        matcherTokenMap.put(inMatcher, Token.TokenType.IN_KEYWORD);
        matcherTokenMap.put(charMatcher, Token.TokenType.CHAR_KEYWORD);
        matcherTokenMap.put(defineMatcher, Token.TokenType.DEFINE_KEYWORD);
        matcherTokenMap.put(multiplicationMatcher, Token.TokenType.MULTIPLICATION_OPERATOR);
        matcherTokenMap.put(moduloMatcher, Token.TokenType.MODULO_OPERATOR);
        matcherTokenMap.put(divisionMatcher, Token.TokenType.DIVISION_OPERATOR);
        matcherTokenMap.put(minusMatcher, Token.TokenType.SUBTRACTION_OPERATOR);
        matcherTokenMap.put(plusMatcher, Token.TokenType.ADDITION_OPERATOR);
        matcherTokenMap.put(greaterThanOrEqualMatcher, Token.TokenType.GREATER_EQUAL_OPERATOR);
        matcherTokenMap.put(lessThanOrEqualMatcher, Token.TokenType.LESS_EQUAL_OPERATOR);
        matcherTokenMap.put(greaterThanMatcher, Token.TokenType.GREATER_OPERATOR);
        matcherTokenMap.put(lessThanMatcher, Token.TokenType.LESS_OPERATOR);
        matcherTokenMap.put(notEqualMatcher, Token.TokenType.NOT_EQUAL_OPERATOR);
        matcherTokenMap.put(equalMatcher, Token.TokenType.EQUAL_OPERATOR);
        matcherTokenMap.put(orMatcher, Token.TokenType.OR_OPERATOR);
        matcherTokenMap.put(andMatcher, Token.TokenType.AND_OPERATOR);
        matcherTokenMap.put(notMatcher, Token.TokenType.NOT_OPERATOR);
        matcherTokenMap.put(expressionCombinerMatcher, Token.TokenType.EXPRESSION_OPERATOR);
        matcherTokenMap.put(trueConstMatcher, Token.TokenType.BOOL_CONSTANT);
        matcherTokenMap.put(falseConstMatcher, Token.TokenType.BOOL_CONSTANT);
        matcherTokenMap.put(intConstMatcher, Token.TokenType.INT_CONSTANT);
        matcherTokenMap.put(floatConstMatcher, Token.TokenType.FLOAT_CONSTANT);
        matcherTokenMap.put(charConstMatcher, Token.TokenType.CHAR_CONSTANT);
        matcherTokenMap.put(nameMatcher, Token.TokenType.IDENTIFIER);
        matcherTokenMap.put(colonMatcher, Token.TokenType.COLON);
        matcherTokenMap.put(commaMatcher, Token.TokenType.COMMA);
        matcherTokenMap.put(semiColonMatcher, Token.TokenType.SEMI_COLON);
        matcherTokenMap.put(closingSquareBracketMatcher, Token.TokenType.CLOSE_SQUARE_BRACKET);
        matcherTokenMap.put(openingSquareBracketMatcher, Token.TokenType.OPEN_SQUARE_BRACKET);
        matcherTokenMap.put(closingCurlyBracketMatcher, Token.TokenType.CLOSE_CURLY_BRACKET);
        matcherTokenMap.put(openingCurlyBracketMatcher, Token.TokenType.OPEN_CURLY_BRACKET);
        matcherTokenMap.put(closingBracketMatcher, Token.TokenType.CLOSE_BRACKET);
        matcherTokenMap.put(openingBracketMatcher, Token.TokenType.OPEN_BRACKET);
        matcherTokenMap.put(bagMatcher, Token.TokenType.BAG_KEYWORD);
        matcherTokenMap.put(fillMatcher, Token.TokenType.FILL_KEYWORD);
        matcherTokenMap.put(dotMatcher, Token.TokenType.DOT);
    }

    public List<Token> getTokens(String program) throws UnknownSymbolException {
        List<Token> tokenList = new ArrayList<>();
        StringBuilder word = new StringBuilder();

        for (int i = 0 ; i < program.length() ; i++) {
            boolean flag = false;
            int maxMatcherCounter = Integer.MAX_VALUE;
            MyMatcher maxMatcher = null;
            word.append(program.charAt(i));
            for (MyMatcher matcher : matchers) {
                Pair<MyMatcher.State, Integer> result = matcher.check(word.toString());
                if (result.first.equals(MyMatcher.State.MATCHED)) {
                    flag = true;
                    if (result.second < maxMatcherCounter) {
                        maxMatcherCounter = result.second;
                        maxMatcher = matcher;
                    }
                } else if (result.first.equals(MyMatcher.State.PARTIAL_MATCH)) {
                    flag = true;
                } else if (result.first.equals(MyMatcher.State.MATCHED_BEFORE)) {
                    if (result.second < maxMatcherCounter) {
                        maxMatcherCounter = result.second;
                        maxMatcher = matcher;
                    }
                }
            }
            if (!flag) {
                if (maxMatcher == null) {
                    throw new UnknownSymbolException(word.toString());
                }
                word.setLength(word.length() - maxMatcherCounter);
                if (!maxMatcher.equals(blankMatcher) &&
                        !maxMatcher.equals(commentMatcher) &&
                        !maxMatcher.equals(multiLineCommentMatcher)) {
                    tokenList.add(new Token(matcherTokenMap.get(maxMatcher), word.toString()));
                }
                word.setLength(0);
                i -= maxMatcherCounter;
                matchers.forEach(MyMatcher::reset);
            }
        }
        return tokenList;
    }
}
