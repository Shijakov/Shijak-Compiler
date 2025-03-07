package com.company;

//import com.company.code_generator.Generator;
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
        String program = """
                define {
                    3.14 >> PI;
                    'a' >> A;
                    6 >> SIX;
                    true >> TRUE;
                }

                fun main(): void {
                    let a: int;
                    output PI;
                    output A;
                    output SIX;
                    9 >> in * 3 >> eq a;
                }
                """;
        try {
            Lexer lexer = new Lexer();
            List<Token> tokens = lexer.getTokens(program);
            ParseTree parseTree = new ParseTree(tokens);
            AbstractSyntaxTree abs = AbstractSyntaxTree.from(parseTree);
//            abs.print();
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            SymbolTable symbolTable = semanticAnalyzer.analyze(abs);
            abs.print();

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