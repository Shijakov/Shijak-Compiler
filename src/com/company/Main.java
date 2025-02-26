package com.company;

import com.company.code_generator.Generator;
import com.company.dev_exceptions.ErrorInGenerationException;
import com.company.dev_exceptions.ScopeNotFoundException;
import com.company.exceptions.*;
import com.company.lexer.Lexer;
import com.company.lexer.Token;
import com.company.parser.abstract_syntax_tree.AbstractSyntaxTree;
import com.company.parser.parse_tree.ParseTree;
import com.company.semantic_analyzer.SemanticAnalyzer;
import com.company.symbol_table.SymbolTable;

import java.util.List;

public class Main {

    public static void main(String[] args) throws ErrorInGenerationException, ScopeNotFoundException {
        Lexer lexer = new Lexer();
        String program = """
                fun main(): void {
                    output (1 == 2) && (2 == (3 + 4 - 5));
                }
                """;
        try {
            List<Token> tokens = lexer.getTokens(program);
            ParseTree parseTree = new ParseTree(tokens);
            AbstractSyntaxTree abs = AbstractSyntaxTree.from(parseTree);
            abs.print();
//            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
//            SymbolTable symbolTable = semanticAnalyzer.analyze(abs);
//            String result = Generator.generate(abs, symbolTable);
//            System.out.println(result);
//        } catch (UnexpectedTokenException | UnknownSymbolException | ConstantWithSameNameExistsException | AttemptToChangeConstValueException | VariableNotDeclaredException | VariableAlreadyDeclaredException | FunctionDefinedMultipleTimesException | TypeMismatchException | InHasNoValueException | InvalidTypesForBinaryOperatorException | FunctionDoesntExistException | InvalidArrayCallException | AttemptToFreeAPrimitiveValueException | InvalidReturnTypeException | NotAllPathsHaveAReturnStatementException | BreakNotInLoopException | ContinueNotInLoopException | CannotAssignValueToPointerException e) {
//            e.printStackTrace(System.err);
//        }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}