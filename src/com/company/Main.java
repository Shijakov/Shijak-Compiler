package com.company;

import com.company.code_generator.Generator;
import com.company.dev_exceptions.ErrorInGenerationException;
import com.company.exceptions.symbol_table.ScopeNotFoundException;
import com.company.lexer.Lexer;
import com.company.lexer.Token;
import com.company.parser.abstract_syntax_tree.AbstractSyntaxTree;
import com.company.parser.parse_tree.ParseTree;
import com.company.semantic_analyzer.SemanticAnalyzer;
import com.company.symbol_table.SymbolTable;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws ErrorInGenerationException, ScopeNotFoundException {
        try {
            String inputCode = new String(Files.readAllBytes(Paths.get("input.shj")));
            String inputForProgram = new String(Files.readAllBytes(Paths.get("input.txt")));
            Scanner scanner = new Scanner(inputForProgram);

            Lexer lexer = new Lexer();
            List<Token> tokens = lexer.getTokens(inputCode);

            ParseTree parseTree = new ParseTree(tokens);

            AbstractSyntaxTree abs = AbstractSyntaxTree.from(parseTree);

            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            SymbolTable symbolTable = semanticAnalyzer.analyze(abs);

            var generator = new Generator(symbolTable);
            String result = generator.generate(abs);

            System.out.println(result);
        }  catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}