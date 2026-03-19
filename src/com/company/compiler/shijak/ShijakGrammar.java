package com.company.compiler.shijak;

import com.company.compiler.common.grammar.Grammar;
import com.company.compiler.common.grammar.GrammarBuilder;
import com.company.compiler.common.grammar.Rule;
import com.company.compiler.common.symbol.NonTerminal;
import com.company.compiler.common.symbol.StartSymbol;
import com.company.compiler.common.symbol.Symbol;

import java.util.List;

import static com.company.compiler.shijak.ShijakNonTerminals.*;
import static com.company.compiler.shijak.ShijakTokens.*;

public class ShijakGrammar {
    private final GrammarBuilder grammarBuilder;

    private ShijakGrammar() {
        grammarBuilder = GrammarBuilder.grammar(PROGRAM);
        addRules();
    }

    private void rule(NonTerminal left, Symbol... right) {
        grammarBuilder.withAdded(new Rule(left, List.of(right)));
    }

    private void addRules() {
        rule(PROGRAM, DEFINITION, FUNCTION_OR_BAG);
        rule(PROGRAM, FUNCTION_OR_BAG);

        rule(DEFINITION, defineToken, openingCurlyBracketToken, DEFINITION_ASSIGNMENT, closingCurlyBracketToken);
        rule(DEFINITION_ASSIGNMENT, PRIMITIVE_CONSTANT, expressionCombinerToken, nameToken, semiColonToken, DEFINITION_ASSIGNMENT);
        rule(DEFINITION_ASSIGNMENT);

        rule(FUNCTION_OR_BAG, FUNCTION, FUNCTION_OR_BAG);
        rule(FUNCTION_OR_BAG, BAG_DEFINITION, FUNCTION_OR_BAG);
        rule(FUNCTION_OR_BAG);

        rule(BAG_DEFINITION, bagToken, nameToken, openingCurlyBracketToken, BAG_DEFINITION_PARAMETER_LIST, closingCurlyBracketToken);

        rule(BAG_DEFINITION_PARAMETER_LIST, nameToken, colonToken, TYPE, BAG_DEFINITION_PARAMETER_LIST_TAIL);
        rule(BAG_DEFINITION_PARAMETER_LIST);
        rule(BAG_DEFINITION_PARAMETER_LIST_TAIL, commaToken, nameToken, colonToken, TYPE, BAG_DEFINITION_PARAMETER_LIST_TAIL);
        rule(BAG_DEFINITION_PARAMETER_LIST_TAIL);

        rule(FUNCTION, functionToken, nameToken, openingBracketToken, PARAM_LIST, closingBracketToken, colonToken, RETURN_TYPE, openingCurlyBracketToken, STATEMENT_LIST, closingCurlyBracketToken);

        rule(PARAM_LIST, nameToken, colonToken, TYPE, PARAM_LIST_TAIL);
        rule(PARAM_LIST);
        rule(PARAM_LIST_TAIL, commaToken, nameToken, colonToken, TYPE, PARAM_LIST_TAIL);
        rule(PARAM_LIST_TAIL);

        rule(STATEMENT_LIST, STATEMENT, STATEMENT_LIST);
        rule(STATEMENT_LIST);

        rule(STATEMENT, WHILE_STATEMENT);
        rule(STATEMENT, IF_STATEMENT);
        rule(STATEMENT, BREAK_STATEMENT);
        rule(STATEMENT, CONTINUE_STATEMENT);
        rule(STATEMENT, EXPRESSION_LIST);
        rule(STATEMENT, DEFINE_VAR);
        rule(STATEMENT, INPUT_STATEMENT);
        rule(STATEMENT, OUTPUT_STATEMENT);
        rule(STATEMENT, ALLOC_ARR);
        rule(STATEMENT, FREE_ARR);
        rule(STATEMENT, RETURN_STATEMENT);
        rule(STATEMENT, FILL_STATEMENT);

        rule(FILL_STATEMENT, fillToken, bagToken, nameToken, expressionCombinerToken, ASSIGNABLE_INSTANCE, semiColonToken);

        rule(DEFINE_VAR, letToken, nameToken, colonToken, TYPE, semiColonToken);

        rule(ALLOC_ARR, allocToken, PRIMITIVE_TYPE, openingSquareBracketToken, MOD_EXPR, closingSquareBracketToken, expressionCombinerToken, ASSIGNABLE_INSTANCE, semiColonToken);
        rule(FREE_ARR, freeToken, ASSIGNABLE_INSTANCE, semiColonToken);

        rule(RETURN_STATEMENT, returnToken, semiColonToken);

        rule(BREAK_STATEMENT, breakToken, semiColonToken);

        rule(CONTINUE_STATEMENT, continueToken, semiColonToken);

        rule(INPUT_STATEMENT, inputToken, ASSIGNABLE_INSTANCE, semiColonToken);

        rule(OUTPUT_STATEMENT, outputToken, EXPRESSION, semiColonToken);

        rule(WHILE_STATEMENT, whileToken, openingBracketToken, EXPRESSION, closingBracketToken, openingCurlyBracketToken, STATEMENT_LIST, closingCurlyBracketToken);

        rule(IF_STATEMENT, ifToken, openingBracketToken, EXPRESSION, closingBracketToken, openingCurlyBracketToken, STATEMENT_LIST, closingCurlyBracketToken, ELIF_STATEMENT, ELSE_STATEMENT);
        rule(ELIF_STATEMENT, elifToken, openingBracketToken, EXPRESSION, closingBracketToken, openingCurlyBracketToken, STATEMENT_LIST, closingCurlyBracketToken, ELIF_STATEMENT);
        rule(ELIF_STATEMENT);
        rule(ELSE_STATEMENT, elseToken, openingCurlyBracketToken, STATEMENT_LIST, closingCurlyBracketToken);
        rule(ELSE_STATEMENT);

        rule(EXPRESSION_LIST, EXPRESSION, EXPRESSION_LIST_TAIL, semiColonToken);

        rule(EXPRESSION_LIST_TAIL, expressionCombinerToken, EXPRESSION_OR_CLOSER);
        rule(EXPRESSION_LIST_TAIL);

        rule(EXPRESSION_OR_CLOSER, CLOSER);
        rule(EXPRESSION_OR_CLOSER, EXPRESSION, EXPRESSION_LIST_TAIL);

        rule(CLOSER, returnToken);
        rule(CLOSER, eqToken, ASSIGNABLE_INSTANCE);

        rule(EXPRESSION, MOD_EXPR, EXPRESSION_TAIL);
        rule(EXPRESSION_TAIL, greaterThanOrEqualToken, MOD_EXPR);
        rule(EXPRESSION_TAIL, lessThanOrEqualToken, MOD_EXPR);
        rule(EXPRESSION_TAIL, lessThanToken, MOD_EXPR);
        rule(EXPRESSION_TAIL, greaterThanToken, MOD_EXPR);
        rule(EXPRESSION_TAIL, equalToken, MOD_EXPR);
        rule(EXPRESSION_TAIL, notEqualToken, MOD_EXPR);
        rule(EXPRESSION_TAIL);

        rule(MOD_EXPR, ADD_EXPR, MOD_EXPR_TAIL);
        rule(MOD_EXPR_TAIL, moduloToken, ADD_EXPR, MOD_EXPR_TAIL);
        rule(MOD_EXPR_TAIL);

        rule(ADD_EXPR, MUL_EXPR, ADD_EXPR_TAIL);
        rule(ADD_EXPR_TAIL, plusToken, MUL_EXPR, ADD_EXPR_TAIL);
        rule(ADD_EXPR_TAIL, minusToken, MUL_EXPR, ADD_EXPR_TAIL);
        rule(ADD_EXPR_TAIL, orToken, MUL_EXPR, ADD_EXPR_TAIL);
        rule(ADD_EXPR_TAIL);

        rule(MUL_EXPR, PRIMARY, MUL_EXPR_TAIL);
        rule(MUL_EXPR_TAIL, multiplicationToken, PRIMARY, MUL_EXPR_TAIL);
        rule(MUL_EXPR_TAIL, divisionToken, PRIMARY, MUL_EXPR_TAIL);
        rule(MUL_EXPR_TAIL, andToken, PRIMARY, MUL_EXPR_TAIL);
        rule(MUL_EXPR_TAIL);

        rule(PRIMARY, INSTANCE);
        rule(PRIMARY, notToken, PRIMARY);
        rule(PRIMARY, openingBracketToken, EXPRESSION, closingBracketToken);
        rule(PRIMARY, PRIMITIVE_CONSTANT);
        rule(PRIMARY, inToken);

        rule(INSTANCE, nameToken, INSTANCE_TAIL);
        rule(INSTANCE_TAIL, openingBracketToken, EXPR_LIST, closingBracketToken);
        rule(INSTANCE_TAIL, ASSIGNABLE_INSTANCE_TAIL);

        rule(ASSIGNABLE_INSTANCE, nameToken, ASSIGNABLE_INSTANCE_TAIL);

        rule(ASSIGNABLE_INSTANCE_TAIL, openingSquareBracketToken, MOD_EXPR, closingSquareBracketToken, DOT_TAIL);
        rule(ASSIGNABLE_INSTANCE_TAIL, DOT_TAIL);

        rule(DOT_TAIL, dotToken, nameToken, ASSIGNABLE_INSTANCE_TAIL);
        rule(DOT_TAIL);

        rule(EXPR_LIST, EXPRESSION, EXPR_LIST_TAIL);
        rule(EXPR_LIST);
        rule(EXPR_LIST_TAIL, commaToken, EXPRESSION, EXPR_LIST_TAIL);
        rule(EXPR_LIST_TAIL);

        rule(RETURN_TYPE, TYPE);
        rule(RETURN_TYPE, voidToken);

        rule(TYPE, PRIMITIVE_TYPE, ARRAY_EMPTY_EXTENSION);

        rule(ARRAY_EMPTY_EXTENSION, openingSquareBracketToken, closingSquareBracketToken);
        rule(ARRAY_EMPTY_EXTENSION);

        rule(PRIMITIVE_TYPE, intToken);
        rule(PRIMITIVE_TYPE, floatToken);
        rule(PRIMITIVE_TYPE, charToken);
        rule(PRIMITIVE_TYPE, boolToken);
        rule(PRIMITIVE_TYPE, bagToken, nameToken);

        rule(PRIMITIVE_CONSTANT, charConstToken);
        rule(PRIMITIVE_CONSTANT, trueConstToken);
        rule(PRIMITIVE_CONSTANT, falseConstToken);
        rule(PRIMITIVE_CONSTANT, intConstToken);
        rule(PRIMITIVE_CONSTANT, floatConstToken);
    }

    public static Grammar get() {
        return (new ShijakGrammar()).grammarBuilder.build();
    }
}
