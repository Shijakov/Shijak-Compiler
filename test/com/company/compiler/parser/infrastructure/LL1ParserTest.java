package com.company.compiler.parser.infrastructure;

import com.company.compiler.common.grammar.GrammarBuilder;
import com.company.compiler.common.symbol.EmptySymbol;
import com.company.compiler.common.symbol.StartSymbol;
import com.company.compiler.common.token.RecognisedToken;
import com.company.compiler.common.token.TerminalToken;
import com.company.compiler.common.token.Token;
import com.company.compiler.parser.model.ParseNode;
import com.company.compiler.common.tree.exceptions.NoNextNodeInTreeBuilderException;
import com.company.compiler.common.tree.infrastructure.TreeBuilder;
import com.company.compiler.common.tree.infrastructure.TreeComparator;
import com.company.compiler.parser.model.ParseTree;
import com.company.compiler.shijak.ShijakGrammar;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static com.company.compiler.helpers.Helpers.*;
import static com.company.compiler.shijak.ShijakNonTerminals.*;
import static com.company.compiler.shijak.ShijakTokens.*;

public class LL1ParserTest {

    private RecognisedToken rToken(Token token) {
        return RecognisedToken.match(token, token.getRegex(), 1);
    }

    private RecognisedToken rToken(Token token, String value) {
        return RecognisedToken.match(token, value, 1);
    }

    @Test
    public void test() throws NoNextNodeInTreeBuilderException {
        var S = new StartSymbol();
        var E = nonTerminal("E");
        var Ep = nonTerminal("Ep");
        var T = nonTerminal("T");
        var Tp = nonTerminal("Tp");
        var F = nonTerminal("F");
        var plus = token("\\+");
        var times = token("\\*");
        var open = token("\\(");
        var close = token("\\)");
        var id = token("id");
        var tt = new TerminalToken();
        var emp = new EmptySymbol();
        RecognisedToken.match(id, "id", 1);

        var recognizedTokens = List.of(
                RecognisedToken.match(id, "id", 1),
                RecognisedToken.match(plus, "+", 1),
                RecognisedToken.match(id, "id", 1),
                RecognisedToken.match(tt, "$", 1)
        );

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

        var parser = new LL1Parser();
        var tree = parser.parse(recognizedTokens, grammar);

        var expected = TreeBuilder.from(new ParseTree(new ParseNode(S)))
                .attachNodes(List.of(node(E), node(tt)))
                .attachNodes(List.of(node(T), node(Ep)))
                .attachNodes(List.of())
                .attachNodes(List.of(node(F), node(Tp)))
                .attachNodes(List.of(node(plus), node(T), node(Ep)))
                .attachNodes(List.of(node(id)))
                .attachNodes(List.of(node(emp)))
                .attachNodes(List.of())
                .attachNodes(List.of(node(F), node(Tp)))
                .attachNodes(List.of(node(emp)))
                .attachNodes(List.of())
                .attachNodes(List.of())
                .attachNodes(List.of(node(id)))
                .attachNodes(List.of(node(emp)))
                .build();

        assertTrue(TreeComparator.areEqual(expected, tree));
    }

    @Test
    public void testSimpleShijakFunction() throws NoNextNodeInTreeBuilderException {
        var recognizedTokens = List.of(
                rToken(functionToken),
                rToken(nameToken, "a"),
                rToken(openingBracketToken),
                rToken(closingBracketToken),
                rToken(colonToken),
                rToken(voidToken),
                rToken(openingCurlyBracketToken),
                rToken(nameToken, "a"),
                rToken(plusToken),
                rToken(nameToken, "b"),
                rToken(expressionCombinerToken),
                rToken(inToken),
                rToken(multiplicationToken),
                rToken(intConstToken, "2"),
                rToken(semiColonToken),
                rToken(closingCurlyBracketToken)
        );

        var tree = (new LL1Parser()).parse(recognizedTokens, ShijakGrammar.get());

        var expected = TreeBuilder.from(new ParseTree(new ParseNode(new StartSymbol())))
                .attachNodes(List.of(node(PROGRAM)))
                .attachNodes(List.of(node(FUNCTION_OR_BAG)))
                .attachNodes(List.of( node(FUNCTION), node(FUNCTION_OR_BAG) ))
                .attachNodes(List.of( node(functionToken), node(nameToken), node(openingBracketToken), node(PARAM_LIST), node(closingBracketToken), node(colonToken), node(RETURN_TYPE), node(openingCurlyBracketToken), node(STATEMENT_LIST), node(closingCurlyBracketToken) ))
                .attachNodes(List.of( node(new EmptySymbol()) ))
                .attachNodes(List.of(  ))
                .attachNodes(List.of(  ))
                .attachNodes(List.of(  ))
                .attachNodes(List.of( node(new EmptySymbol()) ))
                .attachNodes(List.of(  ))
                .attachNodes(List.of(  ))
                .attachNodes(List.of( node(voidToken) ))
                .attachNodes(List.of(  ))
                .attachNodes(List.of( node(STATEMENT), node(STATEMENT_LIST) ))
                .attachNodes(List.of(  ))
                .attachNodes(List.of(  ))
                .attachNodes(List.of(  ))
                .attachNodes(List.of(  ))

                .attachNodes(List.of( node(EXPRESSION_LIST) ))
                .attachNodes(List.of( node(new EmptySymbol()) ))

                .attachNodes(List.of( node(EXPRESSION), node(EXPRESSION_LIST_TAIL) ))
                .attachNodes(List.of(  ))

                .attachNodes(List.of( node(MOD_EXPR), node(EXPRESSION_TAIL) ))
                .attachNodes(List.of( node(expressionCombinerToken), node(EXPRESSION_OR_CLOSER) )) //

                .attachNodes(List.of( node(ADD_EXPR), node(MOD_EXPR_TAIL) )) //
                .attachNodes(List.of( node(new EmptySymbol()) )) //

                .attachNodes(List.of(  ))
                .attachNodes(List.of( node(EXPRESSION), node(EXPRESSION_LIST_TAIL) )) //

                .attachNodes(List.of( node(MUL_EXPR), node(ADD_EXPR_TAIL) )) //
                .attachNodes(List.of( node(new EmptySymbol()) )) //

                .attachNodes(List.of(  ))

                .attachNodes(List.of( node(MOD_EXPR), node(EXPRESSION_TAIL) )) //
                .attachNodes(List.of( node(new EmptySymbol()) )) //

                .attachNodes(List.of( node(PRIMARY), node(MUL_EXPR_TAIL) )) //
                .attachNodes(List.of( node(plusToken), node(MUL_EXPR), node(ADD_EXPR_TAIL) )) //
                .attachNodes(List.of(  ))

                .attachNodes(List.of( node(ADD_EXPR), node(MOD_EXPR_TAIL) )) //
                .attachNodes(List.of( node(new EmptySymbol()) )) //
                .attachNodes(List.of(  ))

                .attachNodes(List.of( node(INSTANCE) )) //
                .attachNodes(List.of( node(new EmptySymbol()) )) //

                .attachNodes(List.of(  ))
                .attachNodes(List.of( node(PRIMARY), node(MUL_EXPR_TAIL) )) //
                .attachNodes(List.of( node(new EmptySymbol()) )) //

                .attachNodes(List.of( node(MUL_EXPR), node(ADD_EXPR_TAIL) )) //
                .attachNodes(List.of( node(new EmptySymbol()) )) //
                .attachNodes(List.of(  ))

                .attachNodes(List.of( node(nameToken), node(INSTANCE_TAIL) )) //
                .attachNodes(List.of(  ))

                .attachNodes(List.of( node(INSTANCE) )) //
                .attachNodes(List.of( node(new EmptySymbol()) )) //
                .attachNodes(List.of(  ))

                .attachNodes(List.of( node(PRIMARY), node(MUL_EXPR_TAIL) )) //
                .attachNodes(List.of( node(new EmptySymbol()) )) //
                .attachNodes(List.of(  ))

                .attachNodes(List.of(  ))
                .attachNodes(List.of( node(ASSIGNABLE_INSTANCE_TAIL) )) //

                .attachNodes(List.of( node(nameToken), node(INSTANCE_TAIL) )) //
                .attachNodes(List.of(  ))

                .attachNodes(List.of( node(inToken) )) //
                .attachNodes(List.of( node(multiplicationToken), node(PRIMARY), node(MUL_EXPR_TAIL) )) //
                .attachNodes(List.of(  ))

                .attachNodes(List.of( node(DOT_TAIL) )) //

                .attachNodes(List.of(  ))
                .attachNodes(List.of( node(ASSIGNABLE_INSTANCE_TAIL) )) //

                .attachNodes(List.of(  ))

                .attachNodes(List.of(  ))
                .attachNodes(List.of( node(PRIMITIVE_CONSTANT) )) //
                .attachNodes(List.of( node(new EmptySymbol()) )) //

                .attachNodes(List.of( node(new EmptySymbol()) )) //

                .attachNodes(List.of( node(DOT_TAIL) )) //

                .attachNodes(List.of( node(intConstToken) )) //
                .attachNodes(List.of(  ))
                .attachNodes(List.of(  ))

                .attachNodes(List.of( node(new EmptySymbol()) )) //
                .attachNodes(List.of(  ))
                .attachNodes(List.of(  ))
                .build();

        assertTrue(TreeComparator.areEqual(expected, tree));
    }
}
